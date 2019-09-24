package com.weddini.throttling.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.throttling")
public class ThrottlingProperties {

    private static final int DEFAULT_LRU_CACHE_CAPACITY = 10000;

    private int lruCacheCapacity = DEFAULT_LRU_CACHE_CAPACITY;
}
