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

package com.weddini.throttling.example;

import com.weddini.throttling.Throttling;
import com.weddini.throttling.ThrottlingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class ThrottledController {

    /**
     * Throttling configuration:
     * <p>
     * allow 3 HTTP GET requests per minute
     * for each unique {@code javax.servlet.http.HttpServletRequest#getRemoteAddr()}
     */
    @GetMapping("/throttledController")
    @Throttling(limit = 3, timeUnit = TimeUnit.MINUTES, type = ThrottlingType.RemoteAddr)
    public ResponseEntity<String> controllerThrottling() {
        log.info("accessing throttled controller");
        return ResponseEntity.ok().body("ok");
    }

    /**
     * Throttling configuration:
     * <p>
     * allow 3 HTTP GET requests per minute
     * for each unique {@code javax.servlet.http.HttpServletRequest#getRemoteAddr()}
     */
    @GetMapping("/throttledControllerLimitString")
    @Throttling(limitString = "${spring.throttling.controller-request-per-minute}",
        timeUnit = TimeUnit.MINUTES, type = ThrottlingType.RemoteAddr)
    public ResponseEntity<String> controllerThrottlingLimitString() {
        log.info("accessing throttled controller");
        return ResponseEntity.ok().body("ok");
    }

}
