package com.weddini.throttling.service;

/**
 * Default implementation of {@link DateProvider}.
 */
public class DateProviderImpl implements DateProvider {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
