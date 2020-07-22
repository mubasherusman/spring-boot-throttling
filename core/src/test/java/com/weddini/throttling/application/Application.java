/*
 * Copyright (C) 2017-2020
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
