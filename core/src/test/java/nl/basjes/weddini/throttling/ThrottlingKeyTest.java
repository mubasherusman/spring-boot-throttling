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

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class ThrottlingKeyTest {

    static class A {
        @Throttling(limit = 5,
            timeUnit = TimeUnit.HOURS,
            type = ThrottlingType.HeaderValue,
            headerName = "X-Forwarded-For")
        public void testMethod() {
        }
    }

    static class B {
        @Throttling(limit = 3,
            timeUnit = TimeUnit.MINUTES,
            type = ThrottlingType.CookieValue,
            cookieName = "JSESSIONID")
        public void testMethod() {
        }
    }

    @Test
    public void testThrottlingKey() throws NoSuchMethodException {

        Method method1 = A.class.getMethod("testMethod");
        Method method2 = B.class.getMethod("testMethod");

        Throttling annotation1 = findAnnotation(method1, Throttling.class);
        Throttling annotation2 = findAnnotation(method1, Throttling.class);

        Assert.assertNotNull(annotation1);
        Assert.assertNotNull(annotation2);

        // method1 + annotation1 + "127.0.0.1"
        ThrottlingKey key1 = ThrottlingKey.builder()
            .method(method1)
            .annotation(annotation1)
            .evaluatedValue("127.0.0.1")
            .build();

        // method1 + annotation1 + "test"
        ThrottlingKey key4 = ThrottlingKey.builder()
            .method(method1)
            .annotation(annotation1)
            .evaluatedValue("test")
            .build();

        Assert.assertNotEquals(key1, key4);

        // method2 + annotation2 + "test"
        ThrottlingKey key2 = ThrottlingKey.builder()
            .method(method2)
            .annotation(annotation2)
            .evaluatedValue("test")
            .build();

        Assert.assertNotEquals(key1, key2);

        // method1 + annotation1 + "127.0.0.1"
        ThrottlingKey key3 = ThrottlingKey.builder()
            .method(method1)
            .annotation(annotation1)
            .evaluatedValue("127.0.0.1")
            .build();

        Assert.assertEquals(key1.hashCode(), key3.hashCode());
        Assert.assertEquals(key1, key3);

    }
}
