package com.parkar.service;


import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class FileSystemCacheServiceImplTest {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemCacheServiceImplTest.class);

    @BeforeClass
    public static void beforeClass() {

    }


    private String cachePath = "cache/";
    private String cacheExtension = ".cache";

    @Test
    public void testAddAndRemoveObject() {

        FileSystemCacheServiceImpl cache = new FileSystemCacheServiceImpl(30, cachePath, cacheExtension, 500);

        cache.put("amazon", "Amazon Web Service");
        cache.put("google", "Google Cloud Provider");

        logger.info("Total Cached are :{}", cache.size());
        assertEquals("Amazon Web Service", cache.get("amazon"));
        assertEquals(2, cache.size());
    }

    @Test
    public void testExpiredObjects() throws InterruptedException {
         /* Configure Cache:
            Time To live = 1 second,
            Interval = 1 second,
        */

        FileSystemCacheServiceImpl cache = new FileSystemCacheServiceImpl(100, cachePath, cacheExtension, 1);
        cache.put("facebook", "Facebook App");
        cache.put("microsoft", "Azure Cloud");

        assertEquals("Facebook App", cache.get("facebook"));
        assertEquals("Azure Cloud", cache.get("microsoft"));

        Thread.sleep(3000);
        assertEquals(0, cache.size());
        assertNotEquals(2, cache.size());

        logger.info("Two objects are added but reached timeToLive. cache.size(): {} ", cache.size());

    }



}
