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

package nl.basjes.weddini.throttling.autoconfigure;

import nl.basjes.weddini.throttling.ThrottlingAspect;
import nl.basjes.weddini.throttling.service.DateProvider;
import nl.basjes.weddini.throttling.service.DateProviderImpl;
import nl.basjes.weddini.throttling.service.ThrottlingEvaluator;
import nl.basjes.weddini.throttling.service.ThrottlingEvaluatorImpl;
import nl.basjes.weddini.throttling.service.ThrottlingService;
import nl.basjes.weddini.throttling.service.ThrottlingServiceImpl;
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
