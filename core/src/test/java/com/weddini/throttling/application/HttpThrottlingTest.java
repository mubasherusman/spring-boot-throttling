package com.weddini.throttling.application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class HttpThrottlingTest {

    private final MediaType textPlainContentType = new MediaType(
        MediaType.TEXT_PLAIN.getType(),
        MediaType.TEXT_PLAIN.getSubtype(),
        StandardCharsets.UTF_8
    );

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private TestDateProvider dateProvider;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testThrottledController() throws Exception {
        RequestPostProcessor postProcessor1 = request -> {
            request.setRemoteAddr("192.168.0.1");
            return request;
        };

        RequestPostProcessor postProcessor2 = request -> {
            request.setRemoteAddr("192.168.0.2");
            return request;
        };

        // 192.168.0.1
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledController")
                .with(postProcessor1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledController")
            .with(postProcessor1))
            .andExpect(status().is(429));

        // 192.168.0.2
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledController")
                .with(postProcessor2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledController")
            .with(postProcessor2))
            .andExpect(status().is(429));

        // virtually wait 1 minute
        dateProvider.addMillis(60 * 1000 + 100);

        // 192.168.0.1
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledController")
                .with(postProcessor1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledController")
            .with(postProcessor1))
            .andExpect(status().is(429));

        // 192.168.0.2
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledController")
                .with(postProcessor2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledController")
            .with(postProcessor2))
            .andExpect(status().is(429));
    }

    @Test
    public void testThrottledControllerLimitString() throws Exception {
        RequestPostProcessor postProcessor1 = request -> {
            request.setRemoteAddr("192.168.0.1");
            return request;
        };

        RequestPostProcessor postProcessor2 = request -> {
            request.setRemoteAddr("192.168.0.2");
            return request;
        };

        // 192.168.0.1
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledControllerLimitString")
                .with(postProcessor1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledControllerLimitString")
            .with(postProcessor1))
            .andExpect(status().is(429));

        // 192.168.0.2
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledControllerLimitString")
                .with(postProcessor2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledControllerLimitString")
            .with(postProcessor2))
            .andExpect(status().is(429));

        // virtually wait 1 minute
        dateProvider.addMillis(60 * 1000 + 100);

        // 192.168.0.1
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledControllerLimitString")
                .with(postProcessor1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledControllerLimitString")
            .with(postProcessor1))
            .andExpect(status().is(429));

        // 192.168.0.2
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/throttledControllerLimitString")
                .with(postProcessor2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(textPlainContentType));
        }

        mockMvc.perform(get("/throttledControllerLimitString")
            .with(postProcessor2))
            .andExpect(status().is(429));
    }

    @Test
    public void testRemoteAddr() throws Exception {

        RequestPostProcessor postProcessor1 = request -> {
            request.setRemoteAddr("192.168.0.1");
            return request;
        };
        RequestPostProcessor postProcessor2 = request -> {
            request.setRemoteAddr("192.168.0.2");
            return request;
        };

        // remoteAddr = 192.168.0.1
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/throttling/remoteAddr/Alex")
                .with(postProcessor1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/remoteAddr/Alex")
            .with(postProcessor1))
            .andExpect(status().is(429));

        // remoteAddr = 192.168.0.2
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/throttling/remoteAddr/Vasya")
                .with(postProcessor2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/remoteAddr/Vasya")
            .with(postProcessor2))
            .andExpect(status().is(429));

        // virtually wait 1 minute
        dateProvider.addMillis(60 * 1000 + 100);

        // remoteAddr = 192.168.0.1
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/throttling/remoteAddr/Alex")
                .with(postProcessor1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/remoteAddr/Alex")
            .with(postProcessor1))
            .andExpect(status().is(429));

        // remoteAddr = 192.168.0.2
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/throttling/remoteAddr/Vasya")
                .with(postProcessor2))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/remoteAddr/Vasya")
            .with(postProcessor2))
            .andExpect(status().is(429));
    }

    @Test
    public void testHeader() throws Exception {
        // remoteAddr = 10.10.10.10
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/throttling/header/Alex")
                .header("X-Forwarded-For", "10.10.10.10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/header/Alex")
            .header("X-Forwarded-For", "10.10.10.10"))
            .andExpect(status().is(429));

        // remoteAddr = 10.10.10.101
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/throttling/header/Vasya")
                .header("X-Forwarded-For", "10.10.10.101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }

        mockMvc.perform(get("/throttling/header/Vasya")
            .header("X-Forwarded-For", "10.10.10.101"))
            .andExpect(status().is(429));

        // virtually wait 1 minute
        dateProvider.addMillis(60 * 1000 + 100);

        // remoteAddr = 10.10.10.10
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/throttling/header/Alex")
                .header("X-Forwarded-For", "10.10.10.10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/header/Alex")
            .header("X-Forwarded-For", "10.10.10.10"))
            .andExpect(status().is(429));

        // remoteAddr = 10.10.10.101
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/throttling/header/Vasya")
                .header("X-Forwarded-For", "10.10.10.101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
        mockMvc.perform(get("/throttling/header/Vasya")
            .header("X-Forwarded-For", "10.10.10.101"))
            .andExpect(status().is(429));
    }

}
