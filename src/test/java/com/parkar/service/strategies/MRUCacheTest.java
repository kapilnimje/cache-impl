package com.parkar.service.strategies;

import com.parkar.service.TwoLevelCache;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.stream.IntStream;

import static com.parkar.service.strategies.StrategyType.MRU;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MRUCacheTest {

    private TwoLevelCache<Integer, String> twoLevelCache;

    @After
    public void clearCache() {
        twoLevelCache.clear();
    }

    @Test
    public void testMoveObjectFromCache() throws IOException {
        twoLevelCache = new TwoLevelCache<>(2, 2, MRU);

        // i=3 - Most Recently Used - will be removed

        IntStream.range(0, 4).forEach(i -> {
            twoLevelCache.put(i, "String " + i);
            assertTrue(twoLevelCache.isObjectExists(i));
            twoLevelCache.get(i);
        });

        twoLevelCache.put(4, "String 4");

        assertTrue(twoLevelCache.isObjectExists(0));
        assertTrue(twoLevelCache.isObjectExists(1));
        assertTrue(twoLevelCache.isObjectExists(2));
        assertFalse(twoLevelCache.isObjectExists(3)); //Most Recently Used - has been removed
        assertTrue(twoLevelCache.isObjectExists(4));

        assertEquals(twoLevelCache.getSize(), 4);
    }
}
