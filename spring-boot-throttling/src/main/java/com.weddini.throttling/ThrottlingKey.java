package com.weddini.throttling;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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
            this.limit = throttling.limit();
            this.type = throttling.type();
            this.timeUnit = throttling.timeUnit();
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
