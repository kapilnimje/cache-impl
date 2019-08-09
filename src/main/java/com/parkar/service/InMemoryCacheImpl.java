package com.parkar.service;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCacheImpl<K extends Serializable, V extends Serializable> implements CacheService<K, V> {

    private final Map<K,V> storageMap;
    private final int capacity;

    public InMemoryCacheImpl(int capacity) {
        this.capacity = capacity;
        this.storageMap = new ConcurrentHashMap<>(capacity);
    }

    @Override
    public V get(K key) {
        return storageMap.get(key);
    }

    @Override
    public void put(K key, V value) {
        storageMap.put(key, value);

    }

    @Override
    public void remove(K key) {
        storageMap.remove(key);

    }

    @Override
    public int getSize() {
        return storageMap.size();
    }

    @Override
    public boolean isObjectExists(K key) {
        return storageMap.containsKey(key);
    }

    @Override
    public boolean hasEmpty() {
        return getSize() < this.capacity;
    }

    @Override
    public void clear() {
        storageMap.clear();
    }
}
