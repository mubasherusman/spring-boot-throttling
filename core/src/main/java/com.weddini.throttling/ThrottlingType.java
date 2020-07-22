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

/**
 * Enumeration of supported throttling types.
 *
 * <p>Used to evaluate method execution context in {@link Throttling} configuration.
 *
 * @author Nikolay Papakha (nikolay.papakha@gmail.com)
 */
public enum ThrottlingType {

    /**
     * Throttling context will be evaluated via the request-scoped bean {@link javax.servlet.http.HttpServletRequest}
     * {@see javax.servlet.http.HttpServletRequest#getRemoteAddr()}
     */
    RemoteAddr,

    /**
     * Throttling context will be evaluated via the request-scoped bean {@link javax.servlet.http.HttpServletRequest}
     * {@see javax.servlet.http.HttpServletRequest#getHeader()}
     */
    HeaderValue,

    /**
     * Throttling context will be evaluated via the request-scoped bean {@link javax.servlet.http.HttpServletRequest}
     * {@see javax.servlet.http.HttpServletRequest#getCookies()}
     */
    CookieValue,

    /**
     * Throttling context will be evaluated via the request-scoped bean {@link javax.servlet.http.HttpServletRequest}
     * {@see javax.servlet.http.HttpServletRequest#getUserPrincipal().getName()}
     */
    PrincipalName,

    /**
     * Throttling context will be evaluated as Spring-EL expression
     * before invoking the protected method
     */
    SpEL

}
