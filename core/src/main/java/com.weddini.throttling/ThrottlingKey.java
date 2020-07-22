package com.weddini.throttling;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

/**
 * Class holding method execution context
 * Used as a key in {@link com.weddini.throttling.cache.Cache}
 *
 * @author Nikolay Papakha (nikolay.papakha@gmail.com)
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ThrottlingKey {

    private final static Map<String, Integer> RESOLVED_STRINGS = new HashMap<>();

    @Getter private final Method method;
    @Getter private final int limit;
    @Getter private final ThrottlingType type;
    @Getter private final TimeUnit timeUnit;
    @Getter private final String evaluatedValue;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Method method;
        private int limit;
        private ThrottlingType type;
        private TimeUnit timeUnit;
        private String evaluatedValue;

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder annotation(Throttling throttling) {
            limit = ofNullable(RESOLVED_STRINGS.get(throttling.limitString()))
                .orElse(throttling.limit());
            type = throttling.type();
            timeUnit = throttling.timeUnit();
            return this;
        }

        public Builder annotation(StringValueResolver embeddedValueResolver, Throttling throttling) {
            limit = Optional.of(throttling.limitString())
                .filter(StringUtils::hasText)
                .map(limitString -> RESOLVED_STRINGS.computeIfAbsent(limitString, s -> {

                    String resolvedLimitString = ofNullable(embeddedValueResolver)
                        .map(resolver -> resolver.resolveStringValue(limitString))
                        .filter(StringUtils::hasText)
                        .orElse(limitString);

                    try {
                        return Integer.parseInt(resolvedLimitString);
                    } catch (RuntimeException ex) {
                        throw new IllegalArgumentException(
                            "Invalid limitString value \"" + resolvedLimitString
                                + "\" - cannot parse into long");
                    }
                }))
                .orElse(throttling.limit());
            type = throttling.type();
            timeUnit = throttling.timeUnit();
            return this;
        }

        public Builder evaluatedValue(String evaluatedValue) {
            this.evaluatedValue = evaluatedValue;
            return this;
        }

        public ThrottlingKey build() {
            return new ThrottlingKey(method, limit, type, timeUnit, evaluatedValue);
        }
    }
}
