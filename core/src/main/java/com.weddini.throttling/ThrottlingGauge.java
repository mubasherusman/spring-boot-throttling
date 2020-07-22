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

import com.weddini.throttling.service.DateProvider;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class holding method calls information
 * Used as a value in {@link com.weddini.throttling.cache.Cache}
 *
 * @author Nikolay Papakha (nikolay.papakha@gmail.com)
 */
@RequiredArgsConstructor
public class ThrottlingGauge {

    private final TimeUnit timeUnit;
    private final int throttleLimit;
    private final DateProvider dateProvider;
    private final ArrayList<Long> callTimestamps = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public boolean throttle() {
        lock.readLock().lock();
        try {
            if (callTimestamps.size() >= throttleLimit) {
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            if (callTimestamps.size() < throttleLimit) {
                callTimestamps.add(dateProvider.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeEldest() {
        long threshold = dateProvider.currentTimeMillis() - timeUnit.toMillis(1);
        lock.writeLock().lock();
        try {
            callTimestamps.removeIf(it -> it < threshold);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
