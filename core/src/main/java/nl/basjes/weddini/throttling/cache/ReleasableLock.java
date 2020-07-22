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

package nl.basjes.weddini.throttling.cache;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;

public class ReleasableLock implements Closeable {

    private final Lock lock;

    private final ThreadLocal<Integer> holdingThreads = new ThreadLocal<>();

    public ReleasableLock(Lock lock) {
        this.lock = lock;
    }

    public ReleasableLock acquire() {
        lock.lock();
        assert addCurrentThread();
        return this;
    }

    @Override
    public void close() {
        lock.unlock();
        assert removeCurrentThread();
    }

    private boolean addCurrentThread() {
        Integer current = holdingThreads.get();
        holdingThreads.set(current == null ? 1 : current + 1);
        return true;
    }

    private boolean removeCurrentThread() {
        Integer count = holdingThreads.get();
        assert count != null && count > 0;
        if (count == 1) {
            holdingThreads.remove();
        } else {
            holdingThreads.set(count - 1);
        }
        return true;
    }

    public boolean isHeldByCurrentThread() {
        Integer count = holdingThreads.get();
        return count != null && count > 0;
    }
}
