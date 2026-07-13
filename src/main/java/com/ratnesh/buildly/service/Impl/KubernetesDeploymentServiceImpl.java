package com.ratnesh.buildly.service.Impl;

import com.ratnesh.buildly.deploy.DeployResponse;
import com.ratnesh.buildly.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesDeploymentServiceImpl implements DeploymentService {

    private final KubernetesClient client;
    private final StringRedisTemplate redisTemplate;

    private static final String NAMESPACE = "buildly";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_PORT = "8090";

    @Override
    public DeployResponse deploy(Long projectId) {

        String domain = "project-" + projectId + ".app.domain.com";

        Pod existingPod = findActivePod(projectId);

        if(existingPod != null) {
            registerRoute(domain, existingPod);
            return new DeployResponse("http://"+domain+":"+REVERSE_PROXY_PORT);
        }

        return claimAndStartNewPod(projectId, domain);
    }

    private DeployResponse claimAndStartNewPod(Long projectId, String domain) {
        Pod pod = client.pods().inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL, IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No idle runners available. Please scale up the runner-pool."));

        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);

        //also as soon as we claim a pod here
        //kubernetes will make more pods(idle) to match no of replicas
        client.pods().inNamespace(NAMESPACE).withName(podName).edit(p -> {
            p.getMetadata().getLabels().put(POOL_LABEL, BUSY);
            p.getMetadata().getLabels().put(PROJECT_LABEL, projectId.toString());
            return p;
        });

        try {
            // Syncer Commands
            // Perform an initial one-time sync of project files
            // from MinIO to the pod's working directory (/app).
            //copy files in minio so latest files of project

            String initialSyncCmd = String.format(
                    "mc mirror --overwrite myminio/projects/%d/ /app/",
                    projectId);

            log.info("Starting initial sync for project {} in pod {}", projectId, podName);
            execCommand(podName, SYNCER_CONTAINER, "sh", "-c", initialSyncCmd);

            // Start a background watcher that continuously mirrors
            // future file changes from MinIO to the pod.
            String watchCmd = String.format(
                    "nohup mc mirror --overwrite --watch myminio/projects/%d/ /app/ > /app/sync.log 2>&1 &",
                    projectId);
            execCommand(podName, SYNCER_CONTAINER, "sh", "-c", watchCmd);

            // Runner Commands
            //nohup -> make process run in bg
            // --host 0.0.0.0 makes the server listen on all network interfaces
            // instead of only localhost, so it can be accessed from outside the container.
            String startCmd = "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 &";

            log.info("Starting dev server for project {}...", projectId);
            execCommand(podName, RUNNER_CONTAINER, "sh", "-c", startCmd);

            registerRoute(domain, pod);

            log.info("Deployment successful: http://{}:{}", domain, REVERSE_PROXY_PORT);
            return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);

        } catch(Exception e) {
            log.error("Deployment failed for project {}. Releasing pod {}.", projectId, podName, e);
            // If deployment fails, delete the pod.
            // Kubernetes will recreate a fresh idle pod,
            // preventing a partially configured pod from being reused.
            client.pods().inNamespace(NAMESPACE).withName(podName).delete();
            throw new RuntimeException("Failed to deploy the project with id: "+projectId);
        }
    }

    private void registerRoute(String domain, Pod pod) {
        String podIp = pod.getStatus().getPodIP();
        if (podIp == null) throw new RuntimeException("Pod is running but has no IP!");
//route:abc123.buildly.dev -> 10.244.1.25:5173
        redisTemplate.opsForValue().set("route:" + domain, podIp + ":5173", 6, TimeUnit.HOURS);
    }


    private void execCommand(String podName, String container, String... command) {
        log.debug("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

        // Used to wait until command execution completes
        CompletableFuture<String> data = new CompletableFuture<>();
        try (
                // Open an exec session with the target container
                ExecWatch ignored = client.pods().inNamespace(NAMESPACE).withName(podName)
                .inContainer(container)
//                  Capture standard output
                .writingOutput(new ByteArrayOutputStream())
                        // Capture error output
                .writingError(new ByteArrayOutputStream())
                        // Listener receives lifecycle callbacks
                .usingListener(new ExecListener() {
                    @Override
                    public void onClose(int code, String reason) {
                        // Notify waiting thread that execution has finished
                        data.complete("Done");
                    }
                })
                .exec(command)) {

//            & means in background
            // If command runs in background (e.g. npm run dev &),
            // don't wait for completion.
            if (command[command.length - 1].trim().endsWith("&")) {
                // Give Kubernetes a little time to start the process
                //wait for .5 sec
                Thread.sleep(500);
            } else {

                // Wait until command finishes (maximum 30 seconds)
                data.get(30, TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            log.error("Exec failed", e);
            throw new RuntimeException("Pod Execution Failed", e);
        }
    }

    Pod findActivePod(Long projectId) {
        return client.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY) // Only find active/busy ones
                .list().getItems().stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }


}
