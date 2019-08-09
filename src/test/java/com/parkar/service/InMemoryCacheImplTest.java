package com.parkar.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class InMemoryCacheImplTest {

    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    private InMemoryCacheImpl<Integer, String> memoryCache;

    @Before
    public void init() {
        memoryCache = new InMemoryCacheImpl<>(3);
    }

    @After
    public void clearCache() {
        memoryCache.clear();
    }

    @Test
    public void testPutGetAndRemoveObject() {
        memoryCache.put(0, VALUE1);
        assertEquals(VALUE1, memoryCache.get(0));
        assertEquals(1, memoryCache.getSize());

        memoryCache.remove(0);
        assertNull(memoryCache.get(0));
    }

    @Test
    public void testDoNotGetObjectIfNotExist() {
        memoryCache.put(0, VALUE1);
        assertEquals(VALUE1, memoryCache.get(0));
        assertNull(memoryCache.get(111));
    }

    @Test
    public void testDoNotRemoveObjectIfNotExist() {
        memoryCache.put(0, VALUE1);
        assertEquals(VALUE1, memoryCache.get(0));
        assertEquals(1, memoryCache.getSize());

        memoryCache.remove(5);
        assertEquals(VALUE1, memoryCache.get(0));
    }

    @Test
    public void testGetCacheSize() {
        memoryCache.put(0, VALUE1);
        assertEquals(1, memoryCache.getSize());

        memoryCache.put(1, VALUE2);
        assertEquals(2, memoryCache.getSize());
    }


    @Test
    public void testIsObjectExits() {
        assertFalse(memoryCache.isObjectExists(0));

        memoryCache.put(0, VALUE1);
        assertTrue(memoryCache.isObjectExists(0));
    }

    @Test
    public void testIsEmpty() {
        memoryCache = new InMemoryCacheImpl<>(5);

        IntStream.range(0, 4).forEach(i -> memoryCache.put(i, "String " + i));
        assertTrue(memoryCache.hasEmpty());

        memoryCache.put(5, "String");
        assertFalse(memoryCache.hasEmpty());
    }

    @Test
    public void testClearCache() {
        IntStream.range(0, 3).forEach(i -> memoryCache.put(i, "String " + i));

        assertEquals(3, memoryCache.getSize());
        memoryCache.clear();
        assertEquals(0, memoryCache.getSize());
    }
}

