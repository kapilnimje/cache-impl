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
public class InMemoryCacheServiceImplTest {

    private final static Logger logger = LoggerFactory.getLogger(InMemoryCacheServiceImplTest.class);

    @BeforeClass
    public static void beforeClass() {

    }

    @Test
    public void testAddAndRemoveObject() {
        /*  Configure Cache:
            Time To Live = 200 Seconds,
            Interval = 500 seconds,
            Max Items = 6
         */
        InMemoryCacheServiceImpl<String, InMemoryCacheServiceImpl.CacheObject> cache =
                new InMemoryCacheServiceImpl<>(100,500,6);

        InMemoryCacheServiceImpl.CacheObject objectAmazon = new InMemoryCacheServiceImpl.CacheObject("Amazon Web Service");
        InMemoryCacheServiceImpl.CacheObject objectGoogle = new InMemoryCacheServiceImpl.CacheObject("Google Cloud Platform");
        InMemoryCacheServiceImpl.CacheObject objectMicrosoft = new InMemoryCacheServiceImpl.CacheObject("Azure Cloud");
        InMemoryCacheServiceImpl.CacheObject objectRedhat = new InMemoryCacheServiceImpl.CacheObject("Redhat Openstack");
        InMemoryCacheServiceImpl.CacheObject objectIbm = new InMemoryCacheServiceImpl.CacheObject("IBM Cloud");
        InMemoryCacheServiceImpl.CacheObject objectApple = new InMemoryCacheServiceImpl.CacheObject("Apple App");

        cache.put("amazon", objectAmazon);
        cache.put("google", objectGoogle);
        cache.put("microsoft", objectMicrosoft);
        cache.put("redhat", objectRedhat);
        cache.put("ibm", objectIbm);
        cache.put("apple", objectApple);

        assertEquals("Amazon Web Service", cache.get("amazon"));
        assertNotEquals("Amazon Web Service", cache.get("google"));

        cache.remove("redhat");
        assertEquals(5, cache.size());

        InMemoryCacheServiceImpl.CacheObject objectTwiter = new InMemoryCacheServiceImpl.CacheObject("Twiter");
        cache.put("twitter", objectTwiter);

        InMemoryCacheServiceImpl.CacheObject objectFacebook = new InMemoryCacheServiceImpl.CacheObject("Facebook");
        cache.put("facebook", objectFacebook);

        assertEquals(6, cache.size());
        assertNotEquals(7, cache.size());

        logger.info("Two objects Added but reached maxItems.. cache.size() : {} ", cache.size());
    }

    @Test
    public void testExpiredObjects() throws InterruptedException {
        /* Configure Cache:
            Time To live = 1 second,
            Interval = 1 second,
            Max Items  = 10
        */

        InMemoryCacheServiceImpl<String, String> cache = new InMemoryCacheServiceImpl<>(1, 1, 10);

        InMemoryCacheServiceImpl.CacheObject objectDevOps = new InMemoryCacheServiceImpl.CacheObject("DevOps");
        InMemoryCacheServiceImpl.CacheObject objectBigData = new InMemoryCacheServiceImpl.CacheObject("BigData");

        cache.put("devops", objectDevOps);
        cache.put("bigdata", objectBigData);

        assertEquals("DevOps", cache.get("devops"));
        assertEquals("BigData", cache.get("bigdata"));

        // Adding 3 seconds sleep.. Both the above objects will be removed from Cache because of
        // timeToLiveInSeconds value 1
        Thread.sleep(3000);

        assertEquals(0, cache.size());
        assertNotEquals(2, cache.size());
        logger.info("Two objects are added but reached timeToLive. cache.size() : {} " , cache.size());
    }

    @Test
    public void testObjectCleanupTime() throws InterruptedException {
        int size = 500000;
        /*  Configure Cache:
            Time To Live  = 100 seconds,
            Interval = 100 seconds,
            Max Items = 500000
        */
        InMemoryCacheServiceImpl<String, InMemoryCacheServiceImpl.CacheObject> cache = new
                InMemoryCacheServiceImpl<>(100, 100, size);

        for (int i = 0; i < size; i++ ) {
            String value = Integer.toString(i);
            InMemoryCacheServiceImpl.CacheObject obj = new InMemoryCacheServiceImpl.CacheObject(value);
            cache.put(value, obj);
        }

        Thread.sleep(200);
        long start = System.currentTimeMillis();
        cache.cleanup();

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        logger.info("Cleanup times for {} objects are {} seconds" , size , finish);

    }
}


