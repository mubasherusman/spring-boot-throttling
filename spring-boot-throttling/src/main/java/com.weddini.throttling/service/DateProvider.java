package com.weddini.throttling.service;

/**
 * Service used to provide the current time in milliseconds. Mainly used for test purpose.
 */
public interface DateProvider {

    /**
     * Returns the current time in milliseconds.
     * @return the current time in milliseconds.
     */
    long currentTimeMillis();

}
