package com.parkar.service.strategies;

/**
 * LFU Strategy - Least Frequently Used
 */

/**
 * LFU Strategy - Least Frequently Used
 * @param <K> - Key Object Type
 */
public class LFUStrategy<K> extends CacheStrategy<K> {

    @Override
    public void putObject(K key) {
        long frequency = 1;

        if (getStorageMap().containsKey(key)) {
            frequency = getStorageMap().get(key) + 1;
        }

        getStorageMap().put(key, frequency);

    }
}
