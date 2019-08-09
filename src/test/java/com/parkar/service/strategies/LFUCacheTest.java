package com.parkar.service.strategies;

import com.parkar.service.TwoLevelCache;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static com.parkar.service.strategies.StrategyType.LFU;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LFUCacheTest {

    private TwoLevelCache<Integer, String> twoLevelCache;

    @After
    public  void clearCache() {
        twoLevelCache.clear();
    }

    @Test
    public void testMoveObjectFromCache() throws IOException {
        twoLevelCache = new TwoLevelCache<>(2, 2, LFU);
        twoLevelCache.put(0, "String 0");
        twoLevelCache.get(0);
        twoLevelCache.get(0);
        twoLevelCache.put(1, "String 1");
        twoLevelCache.get(1); // Least Frequently Used - will be removed
        twoLevelCache.put(2, "String 2");
        twoLevelCache.get(2);
        twoLevelCache.get(2);
        twoLevelCache.put(3, "String 3");
        twoLevelCache.get(3);
        twoLevelCache.get(3);

        assertTrue(twoLevelCache.isObjectExists(0));
        assertTrue(twoLevelCache.isObjectExists(1));
        assertTrue(twoLevelCache.isObjectExists(2));
        assertTrue(twoLevelCache.isObjectExists(3));

        twoLevelCache.put(4, "String 4");
        twoLevelCache.get(4);
        twoLevelCache.get(4);

        assertTrue(twoLevelCache.isObjectExists(0));
        assertFalse(twoLevelCache.isObjectExists(1)); // Least Frequently Used - has been removed
        assertTrue(twoLevelCache.isObjectExists(2));
        assertTrue(twoLevelCache.isObjectExists(3));
        assertTrue(twoLevelCache.isObjectExists(4));
    }

    @Test
    public void testDoNotDeleteObjectIfNotExists() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1, LFU);

        twoLevelCache.put(0, "String 0");
        twoLevelCache.put(1, "String 1");

        twoLevelCache.remove(2);

        assertEquals(twoLevelCache.getSize(), 2);

    }
}
