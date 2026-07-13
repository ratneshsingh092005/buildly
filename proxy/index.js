const http = require('http');
const httpProxy = require('http-proxy');
const Redis = require('ioredis');

// Redis stores mappings like:
// route:project-1.buildly.dev -> 10.244.0.5
const redisUrl = process.env.REDIS_URL || 'redis://redis-service:6379';

// Create Redis client with automatic reconnect support.
// The proxy uses this client to fetch the mapping between
//  preview hostnames and runner pod IP addresses from Redis.
const redis = new Redis(redisUrl, {
    maxRetriesPerRequest: null,
    enableReadyCheck: false,
    retryStrategy(times) {
        // Retry with exponential-like backoff (up to 2 seconds).
        const delay = Math.min(times * 50, 2000);
        console.log(`Redis connection failed. Retrying in ${delay}ms...`);
        return delay;
    }
});

redis.on('error', (err) => {
    console.error('Redis Client Error:', err.message);
});

redis.on('connect', () => {
    console.log('✅ Connected to Redis successfully');
});

// Reverse proxy used to forward HTTP and WebSocket traffic
// to the correct runner pod.
const proxy = httpProxy.createProxyServer({
    ws: true,            // Enable WebSocket proxying (required for Vite HMR)
    xfwd: true,          // Add X-Forwarded-* headers
    changeOrigin: true   // Rewrite Host header for target server
});

/**
 * Look up the runner pod IP for a preview hostname.
 *
 * Example:
 * route:project-3.buildly.dev -> 10.244.0.7
 */
async function getTarget(hostname) {
    try {
        const targetIp = await redis.get(`route:${hostname}`);

        if (targetIp) {
            return targetIp;
        }
    } catch (err) {
        console.error('Redis Error:', err);
    }

    return null;
}

/**
 * Convert an IP into a valid HTTP target.
 *
 * If Redis stores only an IP:
 *      10.244.0.7
 * convert it into:
 *      http://10.244.0.7:5173
 *
 * If Redis already stores host:port:
 *      10.244.0.7:8080
 * keep that port.
 */
const getTargetUrl = (ip) => {
    return ip.includes(':')
        ? `http://${ip}`
        : `http://${ip}:5173`;
};

// Main HTTP server.
// Receives all preview requests and forwards them
// to the correct runner pod.
const server = http.createServer(async (req, res) => {

    // Remove port if present.
    // Example:
    // project-5.buildly.dev:8090
    // ->
    // project-5.buildly.dev
    const rawHost = req.headers.host || '';
    const hostname = rawHost.split(':')[0];

    // Find the pod responsible for this preview.
    const targetIp = await getTarget(hostname);

    // No route exists for this preview.
    if (!targetIp) {
        res.writeHead(404, {
            'Content-Type': 'text/plain'
        });

        return res.end(`Preview not found for ${hostname}.`);
    }

    const target = getTargetUrl(targetIp);

    console.log(`HTTP Proxy: ${hostname} -> ${target}${req.url}`);

    // Forward the request to the runner pod.
    proxy.web(req, res, { target }, (e) => {

        console.error(`Proxy Error (Web): ${hostname}`, e.message);

        if (!res.headersSent) {
            res.writeHead(502);
            res.end('Vite server unavailable...');
        }
    });
});

// Handle WebSocket upgrades.
// Required so Vite Hot Module Replacement (HMR)
// continues working through the proxy.
server.on('upgrade', async (req, socket, head) => {

    const rawHost = req.headers.host || '';
    const hostname = rawHost.split(':')[0];

    const targetIp = await getTarget(hostname);

    if (targetIp) {

        const target = getTargetUrl(targetIp);

        console.log(`WS Upgrade: ${hostname} -> ${target}`);

        // Forward WebSocket connection.
        proxy.ws(req, socket, head, { target }, (e) => {

            console.error(`Proxy Error (WS): ${hostname}`, e.message);

            socket.destroy();
        });

    } else {
        // Reject unknown preview hosts.
        socket.destroy();
    }
});

// Start the wildcard proxy.
server.listen(80, () => {
    console.log('Wildcard Proxy Active on Port 80');
});