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

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/throttling")
public class DemoController {

    private final DemoService demoService;

    @GetMapping(value = "/SpEl/{userName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> spEl(@PathVariable(value = "userName") String userName) {
        return ResponseEntity.ok(demoService.computeWithSpElThrottling(new Model(userName)));
    }

    @GetMapping(value = "/header/{userName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> header(@PathVariable(value = "userName") String userName) {
        return ResponseEntity.ok(demoService.computeWithHttpHeaderThrottling(new Model(userName)));
    }

    @GetMapping(value = "/remoteAddr/{userName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> remoteAddr(@PathVariable(value = "userName") String userName) {
        return ResponseEntity.ok(demoService.computeWithHttpRemoteAddrThrottling(new Model(userName)));
    }
}
