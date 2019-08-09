package com.parkar.service.strategies;

/**
 * MRU Stratey - Most Recently Used
 * @param <K> - Key Object Type
 */
public class MRUStrategy<K> extends CacheStrategy<K> {

    @Override
    public void putObject(K key) {
        getStorageMap().put(key, System.nanoTime());

    }

    @Override
    public K getReplacedKey() {
        getSortedSet().addAll(getStorageMap().entrySet());
        return getSortedSet().last().getKey();
    }

}
