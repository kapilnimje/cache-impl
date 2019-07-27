package com.parkar.service;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class InMemoryCacheServiceImpl<K, T> implements Service {

    private final static Logger logger = LoggerFactory.getLogger(InMemoryCacheServiceImpl.class);

    private long timeToLive;
    private final LRUMap<String, CacheObject> cacheMap;

    /**
     * Custom Cache Object
     */
    protected static class CacheObject<T> {
        private long lastAccessed = System.currentTimeMillis();
        private T value;

        protected CacheObject(T value) {
            this.value = value;
        }
    }

    /**
     * Separate thread for cleanup activity for the expired objects. For the expiration we can timestamp
     * the last access and remove the item when time to live is reached.
     * @param timeToLive - Time To Live value
     * @param timeInterval - Interval for cleanup activity
     * @param maxItems - Maximum number of items in a cache.
     */
    public InMemoryCacheServiceImpl(long timeToLive, final long timeInterval, int maxItems) {
        this.timeToLive = timeToLive * 1000;
        cacheMap = new LRUMap(maxItems);

        if (timeToLive >0 && timeInterval > 0) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timeInterval * 1000);
                        } catch (InterruptedException ex) {
                            logger.error("Interrupted Exception Occurred :", ex);
                        }
                        cleanup();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Get frequently used Object from the Cache
     * @param key - Object key
     * @return - Return Object value
     */
    @Override
    public Object get(String key) {
        synchronized (cacheMap) {
            CacheObject object = cacheMap.get(key);

            if (object == null) {
                return null;
            } else  {
                object.lastAccessed = System.currentTimeMillis();
                return object.value;
            }
        }
    }

    /**
     * Put the object in Cache after storing this into persistent storage
     * @param key - Object key
     * @param value - Object Value
     */
    @Override
    public void put(String key, Object value) {
        CacheObject object = (CacheObject) value;
        synchronized (cacheMap) {
            cacheMap.put(key, object);
        }
    }

    /**
     * Clean up the Cache item last that have last accessed when time to live is reached
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey;

        synchronized (cacheMap) {
            MapIterator iterator = cacheMap.mapIterator();

            deleteKey = new ArrayList<>((cacheMap.size()/2) +1);
            K key;
            CacheObject object;

            while (iterator.hasNext()) {
                key = (K) iterator.next();
                object = (CacheObject) iterator.getValue();

                if ( object != null && (now > (timeToLive + object.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key: deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            Thread.yield();
        }
    }

    /**
     * Remove Object from Cache
     * @param key - Key to remove
     */
    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    /**
     * Get Size of the Cache
     * @return - Return the size of Cache
     */
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }
 }
