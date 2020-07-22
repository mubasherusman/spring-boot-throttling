package com.weddini.throttling.application;

import com.weddini.throttling.service.DateProvider;


public class TestDateProvider implements DateProvider {

    private long currentTimeMillis = System.currentTimeMillis();

    @Override
    public long currentTimeMillis() {
        return currentTimeMillis;
    }

    public void addMillis(long millis) {
        currentTimeMillis += millis;
    }
}
