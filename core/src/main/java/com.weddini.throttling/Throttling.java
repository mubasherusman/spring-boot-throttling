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

package com.weddini.throttling;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Describes throttling attributes on a method.
 *
 * <p>Annotation for specifying method throttling configuration
 * will be evaluated in {@link java.lang.reflect.Proxy} to decide whether a method invocation is allowed or not.
 *
 * @author Nikolay Papakha (nikolay.papakha@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface Throttling {

    /**
     * Returns max number of calls per TimeUnit
     * Default value is 1, i.e. 1 method call per TimeUnit.
     *
     * @return the throttle limit
     */
    int limit() default 1;

    /**
     * Returns max number of calls per TimeUnit No default value.
     *
     * @return the throttle limit as string
     */
    String limitString() default "";

    /**
     * Returns ThrottlingType {@see ThrottlingType}
     * Used to evaluate method execution context.
     * Default value is {@code ThrottlingType.RemoteAddr}
     *
     * @return ThrottlingType
     */
    ThrottlingType type() default ThrottlingType.RemoteAddr;

    /**
     * Returns throttling TimeUnit {@see TimeUnit}
     * Default value is {@code TimeUnit.SECONDS}
     *
     * @return TimeUnit to measure the number of calls
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Returns header name for type = {@code ThrottlingType.HeaderValue}
     * Header value will be evaluated in the request-scoped bean {@link javax.servlet.http.HttpServletRequest}
     *
     * @return name of the HTTP header to be used in throttling
     */
    String headerName() default "";

    /**
     * Returns header name for type = {@code ThrottlingType.CookieValue}
     * Cookie value will be evaluated in the request-scoped bean {@link javax.servlet.http.HttpServletRequest}
     *
     * @return name of the cookie to be used in throttling
     */
    String cookieName() default "";

    /**
     * Applicable for type = {@code ThrottlingType.SpEL}
     *
     * @return the Spring-EL expression to be evaluated as a throttling context
     */
    String expression() default "";

}

