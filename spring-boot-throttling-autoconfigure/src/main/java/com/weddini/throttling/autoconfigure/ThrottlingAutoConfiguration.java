package com.weddini.throttling.autoconfigure;

import com.weddini.throttling.ThrottlingAspect;
import com.weddini.throttling.service.DateProvider;
import com.weddini.throttling.service.DateProviderImpl;
import com.weddini.throttling.service.ThrottlingEvaluator;
import com.weddini.throttling.service.ThrottlingEvaluatorImpl;
import com.weddini.throttling.service.ThrottlingService;
import com.weddini.throttling.service.ThrottlingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ThrottlingProperties.class)
public class ThrottlingAutoConfiguration {

    private final ThrottlingProperties throttlingProperties;

    @Bean
    @ConditionalOnMissingBean
    public ThrottlingEvaluator throttlingEvaluator() {
        return new ThrottlingEvaluatorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DateProvider dateProvider() {
        return new DateProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThrottlingService throttlingService() {
        return new ThrottlingServiceImpl(throttlingProperties.getLruCacheCapacity(), dateProvider());
    }

    @Bean
    public ThrottlingAspect throttlingAspect() {
        return new ThrottlingAspect(throttlingService(), throttlingEvaluator());
    }
}
