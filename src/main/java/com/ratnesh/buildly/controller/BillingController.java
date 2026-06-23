package com.ratnesh.buildly.controller;

import com.ratnesh.buildly.dto.Subscription.*;
import com.ratnesh.buildly.service.PaymentProcessor;
import com.ratnesh.buildly.service.PlanService;
import com.ratnesh.buildly.service.SubscriptionService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j

public class BillingController  {
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;
    @GetMapping("/api/plans")
    public ResponseEntity<PlanResponse> getAllPlans(){
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getMySubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(
            @RequestBody @Valid CheckoutRequest request
    ){
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request));
    }

    @PostMapping("/api/payment/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal(){
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }


    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                try{
                    stripeObject = dataObjectDeserializer.deserializeUnsafe();
                    if (stripeObject==null){
                        log.warn("Failed to deserialize webhook object for event: {}",event.getType());
                        return ResponseEntity.ok().build();
                    }
                } catch (EventDataObjectDeserializationException e) {
                    log.error("Unsafe deserialization failed for event {}:{}",event.getType(),e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }
            }

//            Now extract metadata only if it's a checkout session
            Map<String,String> metadata = new HashMap<>();
            if (stripeObject instanceof Session session){
                metadata = session.getMetadata();
            }

            //pass to processor
            paymentProcessor.handleWebhookEvent(event.getType(),stripeObject,metadata);
            return ResponseEntity.ok().build();


        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
