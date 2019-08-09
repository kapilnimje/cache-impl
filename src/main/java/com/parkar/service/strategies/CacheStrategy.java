package com.parkar.service.strategies;

import jdk.nashorn.internal.objects.annotations.Getter;

import java.util.*;

public abstract class CacheStrategy<K> {

    private final Map<K, Long> storageMap = new TreeMap<>();
    private final SortedSet<Map.Entry<K, Long>> sortedSet;

    public abstract void putObject(K key);


    public CacheStrategy() {
        sortedSet = new TreeSet<>(new Comparator<Map.Entry<K, Long>>() {
            @Override
            public int compare(Map.Entry<K, Long> e1,
                               Map.Entry<K, Long> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });

        sortedSet.addAll(storageMap.entrySet());
    }

    public void removeObject(K key) {
        if (isObjectExists(key)) {
            storageMap.remove(key);
        }

    }

    public boolean isObjectExists(K key) {
        return storageMap.containsKey(key);
    }

    public K getReplacedKey() {
        sortedSet.addAll(storageMap.entrySet());
        return sortedSet.first().getKey();
    }

    public void clear() {
        storageMap.clear();
    }

    public Map<K, Long> getStorageMap() {
        return storageMap;
    }

    public SortedSet<Map.Entry<K, Long>> getSortedSet() {
        return sortedSet;
    }
}
