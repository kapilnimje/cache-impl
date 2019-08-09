package com.parkar.service;

import com.parkar.service.strategies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

public class TwoLevelCache<K extends Serializable, V extends Serializable> implements CacheService<K, V> {

    private final static Logger logger = LoggerFactory.getLogger(TwoLevelCache.class);

    private final InMemoryCacheImpl<K, V> firstLevelCache;
    private final FileSystemCacheImpl<K, V> secondLevelCache;
    private final CacheStrategy<K> strategy;

    public TwoLevelCache(final int memoryCapacity, final int fileCapacity, final StrategyType strategyType)
            throws IOException {
        this.firstLevelCache = new InMemoryCacheImpl<>(memoryCapacity);
        this.secondLevelCache = new FileSystemCacheImpl<>(fileCapacity);
        this.strategy = getStrategy(strategyType);
    }

    public TwoLevelCache(final int memoryCapacity, final int fileCapacity) throws IOException {
        this.firstLevelCache = new InMemoryCacheImpl<>(memoryCapacity);
        this.secondLevelCache = new FileSystemCacheImpl<>(fileCapacity);
        this.strategy = getStrategy(StrategyType.LFU);
    }

    private CacheStrategy<K> getStrategy(StrategyType strategyType) {
        switch (strategyType) {
            case LRU:
                return new LRUStrategy<>();
            case MRU:
                return new MRUStrategy<>();
            case LFU:
            default:
                return new LFUStrategy<>();
        }
    }

    @Override
    public synchronized V get(K key) {
        if (firstLevelCache.isObjectExists(key)) {
            strategy.putObject(key);
            return firstLevelCache.get(key);
        } else if (secondLevelCache.isObjectExists(key)) {
            strategy.putObject(key);
            return secondLevelCache.get(key);
        }
        return null;
    }

    @Override
    public synchronized void put(K key, V value) {
        if ( firstLevelCache.isObjectExists(key) || firstLevelCache.hasEmpty()) {
            logger.debug(String.format("Put object with key %s to the 1st level", key));
            firstLevelCache.put(key, value);

            if (secondLevelCache.isObjectExists(key)) {
                secondLevelCache.remove(key);
            }
        } else if ( secondLevelCache.isObjectExists(key) || secondLevelCache.hasEmpty()) {
            logger.debug(String.format("Put Object with key %s to the 2nd level", key));
            secondLevelCache.put(key, value);
        } else {
            replaceObject(key, value);
        }

        if (!strategy.isObjectExists(key)) {
            logger.debug(String.format("Put Object with key %s to strategy", key));
            strategy.putObject(key);
        }
    }

    private void replaceObject(K key, V value) {
        K replacedKey = strategy.getReplacedKey();
        if ( firstLevelCache.isObjectExists(replacedKey)) {
            logger.debug(String.format("Replace object with key %s from 1st level", replacedKey));
            firstLevelCache.remove(replacedKey);
            firstLevelCache.put(key, value);
        } else if ( secondLevelCache.isObjectExists(replacedKey)) {
            logger.debug(String.format("Reeplace object with key %s from 2nd Level", replacedKey));
            secondLevelCache.remove(replacedKey);
            secondLevelCache.put(key, value);
        }
    }

    @Override
    public synchronized void remove(K key) {
        if(firstLevelCache.isObjectExists(key)) {
            logger.debug(String.format("Remove Object with key %s from 1st level", key));
            firstLevelCache.remove(key);
        }
        if ( secondLevelCache.isObjectExists(key)) {
            logger.debug(String.format("Remove Object with key %s from 2nd level", key));
            secondLevelCache.remove(key);
        }
        strategy.removeObject(key);
    }

    @Override
    public int getSize() {
        return firstLevelCache.getSize() + secondLevelCache.getSize();
    }

    @Override
    public boolean isObjectExists(K key) {
        return firstLevelCache.isObjectExists(key) || secondLevelCache.isObjectExists(key);
    }

    @Override
    public synchronized boolean hasEmpty() {
        return firstLevelCache.hasEmpty() || secondLevelCache.hasEmpty();
    }

    @Override
    public void clear() {
        firstLevelCache.clear();
        secondLevelCache.clear();
        strategy.clear();
    }

    public InMemoryCacheImpl<K, V> getFirstLevelCache() {
        return firstLevelCache;
    }

    public FileSystemCacheImpl<K, V> getSecondLevelCache() {
        return secondLevelCache;
    }

    public CacheStrategy<K> getStrategy() {
        return strategy;
    }
}
