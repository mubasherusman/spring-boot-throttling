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

import nl.basjes.weddini.throttling.service.DateProviderImpl;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class ThrottlingGaugeTest {

    @Test
    public void testThrottlingGauge() throws InterruptedException {
        ThrottlingGauge gauge = new ThrottlingGauge(TimeUnit.SECONDS, 1, new DateProviderImpl());

        gauge.removeEldest();
        Assert.isTrue(gauge.throttle(), "Should be ok with the first call");

        gauge.removeEldest();
        Assert.isTrue(!gauge.throttle(), "Shouldn't be ok with the next call");

        gauge.removeEldest();
        Assert.isTrue(!gauge.throttle(), "Shouldn't be ok with the next call");

        gauge.removeEldest();
        Assert.isTrue(!gauge.throttle(), "Shouldn't be ok with the next call");

        Thread.sleep(1100);

        gauge.removeEldest();
        Assert.isTrue(gauge.throttle(), "Should be ok with the call after sleep 1 sec.");
    }

}
