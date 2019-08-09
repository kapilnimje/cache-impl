package com.parkar.service;

public interface CacheService<K, V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    int getSize();

    boolean isObjectExists(K key);

    boolean hasEmpty();

    void clear();

}
