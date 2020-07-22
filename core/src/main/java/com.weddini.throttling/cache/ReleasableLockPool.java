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

package com.weddini.throttling.cache;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReleasableLockPool {

    private final ArrayList<Tuple<ReleasableLock, ReleasableLock>> locks;

    public ReleasableLockPool(int poolSize) {
        locks = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            ReadWriteLock lock = new ReentrantReadWriteLock();
            ReleasableLock readLock = new ReleasableLock(lock.readLock());
            ReleasableLock writeLock = new ReleasableLock(lock.writeLock());
            locks.add(i, Tuple.tuple(readLock, writeLock));
        }
    }

    public ReleasableLock getReadLockFor(Object resource) {
        return locks.get(resourceToIndex(resource)).v1();
    }

    public ReleasableLock getWriteLockFor(Object resource) {
        return locks.get(resourceToIndex(resource)).v2();
    }

    private int resourceToIndex(Object resource) {
        int hashCode = resource.hashCode();
        return Math.abs(hashCode) % locks.size();
    }

}
