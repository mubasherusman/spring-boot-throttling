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

package nl.basjes.weddini.throttling;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import nl.basjes.weddini.throttling.cache.Cache;
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
 * Used as a key in {@link Cache}
 *
 * @author Nikolay Papakha (nikolay.papakha@gmail.com)
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ThrottlingKey {

    private static final Map<String, Integer> RESOLVED_STRINGS = new HashMap<>();

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
