package com.parkar.service;

import com.parkar.service.strategies.StrategyType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class TwoLevelCacheTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    private TwoLevelCache<Integer, String> twoLevelCache;

    @Before
    public void init() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1);
    }

    @After
    public void clearCache() {
        twoLevelCache.clear();
    }

    @Test
    public void testPutGetAndRemoveObject() {
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(1, twoLevelCache.getSize());

        twoLevelCache.remove(0);
        assertNull(twoLevelCache.get(0));
    }

    @Test
    public void testRemoveObjectFromFirstLevel() {
        twoLevelCache.put(0, VALUE1);
        twoLevelCache.put(1, VALUE2);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(1));

        twoLevelCache.remove(0);

        assertNull(twoLevelCache.getFirstLevelCache().get(0));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(1));
    }

    @Test
    public void testRemoveObjectFromSecondLevel() {
        twoLevelCache.put(0, VALUE1);
        twoLevelCache.put(1, VALUE2);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
        assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(1));

        twoLevelCache.remove(1);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
        assertNull(twoLevelCache.getSecondLevelCache().get(1));
    }

    @Test
    public void testDoNotGetObjectFromCacheIfNotExists() {
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertNull(twoLevelCache.get(111));
    }

    @Test
    public void testRemoveDuplicatedObjectFromSecondLevelWhenFirstLevelHasEmptyPlace() {
        assertTrue(twoLevelCache.getFirstLevelCache().hasEmpty());

        twoLevelCache.getSecondLevelCache().put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getSecondLevelCache().get(0));

        twoLevelCache.put(0, VALUE1);

        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectExists(0));
    }

    @Test
    public void testGetCacheSize() {
        twoLevelCache.put(0, VALUE1);
        assertEquals(1, twoLevelCache.getSize());

        twoLevelCache.put(1, VALUE2);
        assertEquals(2, twoLevelCache.getSize());
    }

    @Test
    public void testIsObjectPresent() {
        assertFalse(twoLevelCache.isObjectExists(0));

        twoLevelCache.put(0, VALUE1);
        assertTrue(twoLevelCache.isObjectExists(0));
    }

    @Test
    public void testIsEmptyPlace() {
        assertFalse(twoLevelCache.isObjectExists(0));
        twoLevelCache.put(0, VALUE1);
        assertTrue(twoLevelCache.hasEmpty());

        twoLevelCache.put(1, VALUE2);
        assertFalse(twoLevelCache.hasEmpty());
    }

    @Test
    public void testClearCache() {
        twoLevelCache.put(0, VALUE1);
        twoLevelCache.put(1, VALUE2);

        assertEquals(2, twoLevelCache.getSize());
        assertTrue(twoLevelCache.getStrategy().isObjectExists(0));
        assertTrue(twoLevelCache.getStrategy().isObjectExists(1));

        twoLevelCache.clear();

        assertEquals(0, twoLevelCache.getSize());
        assertFalse(twoLevelCache.getStrategy().isObjectExists(0));
        assertFalse(twoLevelCache.getStrategy().isObjectExists(1));
    }

    @Test
    public void testUseLRUStrategy() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1, StrategyType.LRU);
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectExists(0));
    }

    @Test
    public void testUseMRUStrategy() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1, StrategyType.MRU);
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
        assertFalse(twoLevelCache.getSecondLevelCache().isObjectExists(0));
    }

}
