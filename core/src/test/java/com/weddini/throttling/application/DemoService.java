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

import com.weddini.throttling.Throttling;
import com.weddini.throttling.ThrottlingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DemoService {

    /**
     * Throttling configuration:
     * <p>
     * allow 3 method calls per minute
     * for each userName in model object
     * passed as parameter
     */
    @Throttling(limit = 3,
        timeUnit = TimeUnit.MINUTES,
        type = ThrottlingType.SpEL,
        expression = "#model.userName")
    public String computeWithSpElThrottling(Model model) {
        log.info("computeWithSpElThrottling..., userName = {}", model.getUserName());
        return model.getUserName();
    }

    /**
     * Throttling configuration:
     * <p>
     * allow 10 method calls per minute
     * for each unique {@code javax.servlet.http.HttpServletRequest#getHeader()}
     */
    @Throttling(limit = 10,
        timeUnit = TimeUnit.MINUTES,
        type = ThrottlingType.HeaderValue,
        headerName = "X-Forwarded-For")
    public String computeWithHttpHeaderThrottling(Model model) {
        log.info("computeWithHttpHeaderThrottling..., userName = {}", model.getUserName());
        return model.getUserName();
    }

    /**
     * Throttling configuration:
     * <p>
     * allow 5 method calls per minute
     * for each unique {@code javax.servlet.http.HttpServletRequest#getRemoteAddr()}
     */
    @Throttling(limit = 5, timeUnit = TimeUnit.MINUTES)
    public String computeWithHttpRemoteAddrThrottling(Model model) {
        log.info("computeWithHttpRemoteAddrThrottling..., userName = {}", model.getUserName());
        return model.getUserName();
    }
}
