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

public class RemovalNotification<K, V> {
    public enum RemovalReason {REPLACED, INVALIDATED, EVICTED}

    private final K key;
    private final V value;
    private final RemovalReason removalReason;

    public RemovalNotification(K key, V value, RemovalReason removalReason) {
        this.key = key;
        this.value = value;
        this.removalReason = removalReason;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public RemovalReason getRemovalReason() {
        return removalReason;
    }

}
