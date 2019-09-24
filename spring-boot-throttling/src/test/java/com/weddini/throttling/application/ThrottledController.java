package com.weddini.throttling.application;

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

}
