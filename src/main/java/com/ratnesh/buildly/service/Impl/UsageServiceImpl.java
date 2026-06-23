package com.ratnesh.buildly.service.Impl;

import com.ratnesh.buildly.dto.Subscription.PlanLimitResponse;
import com.ratnesh.buildly.dto.Subscription.UsageTodayResponse;
import com.ratnesh.buildly.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId) {
        return null;
    }
}
