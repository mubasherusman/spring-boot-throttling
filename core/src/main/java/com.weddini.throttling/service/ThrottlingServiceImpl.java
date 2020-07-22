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

package com.weddini.throttling.service;

import com.weddini.throttling.ThrottlingGauge;
import com.weddini.throttling.ThrottlingKey;
import com.weddini.throttling.cache.Cache;
import com.weddini.throttling.cache.CacheBuilder;
import com.weddini.throttling.cache.CacheLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutionException;

public class ThrottlingServiceImpl implements ThrottlingService {

    private final Log logger = LogFactory.getLog(getClass());

    private final Cache<ThrottlingKey, ThrottlingGauge> cache;
    private final CacheLoader<ThrottlingKey, ThrottlingGauge> gaugeLoader;

    public ThrottlingServiceImpl(int cacheSize, DateProvider dateProvider) {
        cache = CacheBuilder.<ThrottlingKey, ThrottlingGauge>builder()
            .setMaximumWeight(cacheSize)
            .build();
        gaugeLoader = key -> new ThrottlingGauge(key.getTimeUnit(), key.getLimit(), dateProvider);
    }

    @Override
    public boolean throttle(ThrottlingKey key, String evaluatedValue) {

        try {

            ThrottlingGauge gauge = cache.computeIfAbsent(key, gaugeLoader);
            gauge.removeEldest();
            return gauge.throttle();

        } catch (ExecutionException e) {
            if (logger.isErrorEnabled()) {
                logger.error("exception occurred while calculating throttle value", e);
            }
        }

        return true;
    }

}
