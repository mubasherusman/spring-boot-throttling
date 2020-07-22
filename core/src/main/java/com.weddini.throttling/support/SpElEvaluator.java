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

package com.weddini.throttling.support;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class handling the SpEL expression parsing. Meant to be used
 * as a reusable, thread-safe component.
 *
 * @author Nikolay Papakha (nikolay.papakha@gmail.com)
 */
public class SpElEvaluator extends CachedExpressionEvaluator {

    private final ParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final Map<ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<>(64);

    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

    public String evaluate(String expression, Object object, Object[] args, Class<?> clazz, Method method) {
        if (args == null) {
            return null;
        }
        EvaluationContext evaluationContext = createEvaluationContext(object, clazz, method, args);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, clazz);
        return condition(expression, methodKey, evaluationContext);
    }

    private EvaluationContext createEvaluationContext(Object object, Class<?> targetClass, Method method, Object[] args) {
        Method targetMethod = getTargetMethod(targetClass, method);
        ExpressionRootObject root = new ExpressionRootObject(object, args);
        return new MethodBasedEvaluationContext(root, targetMethod, args, paramNameDiscoverer);
    }

    private String condition(String conditionExpression, AnnotatedElementKey elementKey, EvaluationContext evalContext) {
        String result = null;
        Expression expression = getExpression(conditionCache, elementKey, conditionExpression);
        if (expression != null) {
            try {
                result = expression.getValue(evalContext, String.class);
            } catch (NullPointerException ignore) {
            }
        }
        return result;
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            if (targetMethod == null) {
                targetMethod = method;
            }
            targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }

    static class ExpressionRootObject {
        private final Object object;
        private final Object[] args;

        ExpressionRootObject(Object object, Object[] args) {
            this.object = object;
            this.args = args;
        }

        public Object getObject() {
            return object;
        }

        public Object[] getArgs() {
            return args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExpressionRootObject)) return false;

            ExpressionRootObject that = (ExpressionRootObject) o;

            if (!Objects.equals(object, that.object)) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(args, that.args);
        }

        @Override
        public int hashCode() {
            int result = object != null ? object.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }
}
