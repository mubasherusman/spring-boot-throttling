package com.weddini.throttling.example;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/SpEl/{userName}")
    public ResponseEntity<Model> spEl(@PathVariable(value = "userName") String userName) {
        return ResponseEntity.ok(demoService.computeWithSpElThrottling(new Model(userName)));
    }

    @GetMapping("/header/{userName}")
    public ResponseEntity<Model> header(@PathVariable(value = "userName") String userName) {
        return ResponseEntity.ok(demoService.computeWithHttpHeaderThrottling(new Model(userName)));
    }

    @GetMapping("/remoteAddr/{userName}")
    public ResponseEntity<Model> remoteAddr(@PathVariable(value = "userName") String userName) {
        return ResponseEntity.ok(demoService.computeWithHttpRemoteAddrThrottling(new Model(userName)));
    }
}
