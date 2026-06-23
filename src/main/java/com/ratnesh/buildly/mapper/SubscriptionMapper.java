package com.ratnesh.buildly.mapper;

import com.ratnesh.buildly.dto.Subscription.PlanResponse;
import com.ratnesh.buildly.dto.Subscription.SubscriptionResponse;
import com.ratnesh.buildly.entity.Plan;
import com.ratnesh.buildly.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
