package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.Subscription.CheckoutRequest;
import com.ratnesh.buildly.dto.Subscription.CheckoutResponse;
import com.ratnesh.buildly.dto.Subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
