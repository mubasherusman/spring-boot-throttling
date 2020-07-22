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

import nl.basjes.weddini.throttling.service.ThrottlingEvaluator;
import nl.basjes.weddini.throttling.service.ThrottlingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Aspect
@Component
@RequiredArgsConstructor
public class ThrottlingAspect implements EmbeddedValueResolverAware {

    private static final Log LOGGER = LogFactory.getLog(ThrottlingAspect.class);

    private final ThrottlingService   service;
    private final ThrottlingEvaluator evaluator;

    private StringValueResolver stringValueResolver;
    private Map<String, Optional<Throttling>> jointPointsCache = new HashMap<>();

    @Before("@annotation(nl.basjes.weddini.throttling.Throttling)")
    public void throttle(JoinPoint joinPoint) {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.info("Evaluating method with Throttling annotation : " + joinPoint.getSignature().toShortString());
        }

        String signature = joinPoint.getSignature().toString();
        Method method = getMethod(joinPoint);

        jointPointsCache.computeIfAbsent(signature, s -> getAnnotation(method));

        jointPointsCache.get(signature)
            .ifPresent(annotation -> {
                String evaluatedValue = evaluator.evaluate(annotation, joinPoint.getThis(), joinPoint.getSignature().getDeclaringType(), method, joinPoint.getArgs());

                ThrottlingKey key = ThrottlingKey.builder()
                    .method(method)
                    .annotation(stringValueResolver, annotation)
                    .evaluatedValue(evaluatedValue)
                    .build();

                boolean isAllowed = service.throttle(key, evaluatedValue);

                if (!isAllowed) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("cannot proceed with a method call due to @Throttling configuration, type="
                            + annotation.type() + ", value=" + evaluatedValue);
                    }
                    throw new ThrottlingException();
                }
            });
    }

    private static Optional<Throttling> getAnnotation(Method method) {
        return Optional.ofNullable(findAnnotation(method, Throttling.class));
    }

    private static Method getMethod(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }
}
