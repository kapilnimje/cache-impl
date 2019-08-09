package com.parkar.service.strategies;


/**
 * LRU Strategy - Least Recently Used
 * @param <K> - Key Object Type
 */
public class LRUStrategy<K> extends CacheStrategy<K> {
    @Override
    public void putObject(K key) {
        getStorageMap().put(key, System.nanoTime());
    }
}
