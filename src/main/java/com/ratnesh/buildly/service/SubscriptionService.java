package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.Subscription.SubscriptionResponse;
import com.ratnesh.buildly.enums.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getMySubscription();


    void activateSubscription(Long userId, Long planId, String gatewaySubscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String gatewaySubscriptionId);

    boolean canCreateNewProject();
}
