package com.weddini.throttling;

import com.weddini.throttling.service.ThrottlingEvaluator;
import com.weddini.throttling.service.ThrottlingService;
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

    private final ThrottlingService service;
    private final ThrottlingEvaluator evaluator;

    private StringValueResolver stringValueResolver;
    private Map<String, Optional<Throttling>> jointPointsCache = new HashMap<>();

    @Before("@annotation(Throttling)")
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
