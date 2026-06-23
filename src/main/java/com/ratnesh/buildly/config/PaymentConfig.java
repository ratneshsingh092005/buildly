package com.ratnesh.buildly.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Value("${stripe.api.secret}")
    private String stripeSecretKey;

    @PostConstruct      // runs once when application starts
    public void init(){
        //stripe sdk initialised
        Stripe.apiKey = stripeSecretKey;
    }

}
