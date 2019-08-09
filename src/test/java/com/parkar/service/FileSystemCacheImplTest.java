package com.parkar.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class FileSystemCacheImplTest {

    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    private FileSystemCacheImpl<Integer, String> fileSystemCache;

    @Before
    public void init() throws IOException {
        fileSystemCache = new FileSystemCacheImpl<>();
    }

    @After
    public void clearCache() {
        fileSystemCache.clear();
    }

    @Test
    public void testPutGetAndRemoveObject() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.get(0));
        assertEquals(1, fileSystemCache.getSize());

        fileSystemCache.remove(0);
        assertNull(fileSystemCache.get(0));
    }

    @Test
    public void testDoNotGetObjectIfNotExist() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.get(0));
        assertNull(fileSystemCache.get(111));
    }

    @Test
    public void testDoNotRemoveObjectIfNotExist() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(VALUE1, fileSystemCache.get(0));
        assertEquals(1, fileSystemCache.getSize());

        fileSystemCache.remove(5);
        assertEquals(VALUE1, fileSystemCache.get(0));
    }

    @Test
    public void testGetCacheSize() {
        fileSystemCache.put(0, VALUE1);
        assertEquals(1, fileSystemCache.getSize());

        fileSystemCache.put(1, VALUE2);
        assertEquals(2, fileSystemCache.getSize());
    }

    @Test
    public void testIsObjectExists() {
        assertFalse(fileSystemCache.isObjectExists(0));

        fileSystemCache.put(0, VALUE1);
        assertTrue(fileSystemCache.isObjectExists(0));
    }

    @Test
    public void testIsEmptyPlace() throws IOException {
        fileSystemCache = new FileSystemCacheImpl<>(5);

        IntStream.range(0, 4).forEach(i -> fileSystemCache.put(i, "String " + i));
        assertTrue(fileSystemCache.hasEmpty());
        fileSystemCache.put(5, "String");
        assertFalse(fileSystemCache.hasEmpty());
    }

    @Test
    public void testClearCache() {
        IntStream.range(0, 3).forEach(i -> fileSystemCache.put(i, "String " + i));

        assertEquals(3, fileSystemCache.getSize());
        fileSystemCache.clear();
        assertEquals(0, fileSystemCache.getSize());
    }

}
