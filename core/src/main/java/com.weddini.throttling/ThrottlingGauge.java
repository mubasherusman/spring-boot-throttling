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
