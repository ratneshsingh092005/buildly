package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.Subscription.PlanLimitResponse;
import com.ratnesh.buildly.dto.Subscription.UsageTodayResponse;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId);
}
