package com.weddini.throttling.application;

import com.weddini.throttling.ThrottlingAspect;
import com.weddini.throttling.service.ThrottlingEvaluator;
import com.weddini.throttling.service.ThrottlingEvaluatorImpl;
import com.weddini.throttling.service.ThrottlingService;
import com.weddini.throttling.service.ThrottlingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ThrottlingEvaluator throttlingEvaluator() {
        return new ThrottlingEvaluatorImpl();
    }

    @Bean
    public TestDateProvider dateProvider() {
        return new TestDateProvider();
    }

    @Bean
    public ThrottlingService throttlingService() {
        return new ThrottlingServiceImpl(10000, dateProvider());
    }

    @Bean
    public ThrottlingAspect throttlingAspect() {
        return new ThrottlingAspect(throttlingService(), throttlingEvaluator());
    }

}
