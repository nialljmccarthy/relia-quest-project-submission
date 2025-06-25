package com.reliaquest.api.config;

import com.reliaquest.api.exception.TooManyRequestsException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Value("${retry.maxAttempts:3}")
    private int maxAttempts;

    @Value("${retry.backoffDelay:2000}")
    private long backoffDelay;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy defaultRetryPolicy = new SimpleRetryPolicy();
        defaultRetryPolicy.setMaxAttempts(maxAttempts);

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(TooManyRequestsException.class, true);

        SimpleRetryPolicy customRetryPolicy = new SimpleRetryPolicy(3, retryableExceptions, true);
        retryTemplate.setRetryPolicy(customRetryPolicy);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backoffDelay);

        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
