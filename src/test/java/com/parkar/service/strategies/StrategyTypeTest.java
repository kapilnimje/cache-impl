package com.parkar.service.strategies;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class StrategyTypeTest {


    @Test
    public void testGetCorrectValueFromEnum() {
        assertEquals(StrategyType.LFU, StrategyType.valueOf("LFU"));
        assertEquals(StrategyType.LRU, StrategyType.valueOf("LRU"));
        assertEquals(StrategyType.MRU, StrategyType.valueOf("MRU"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeDoesNotExistsError() {
        StrategyType.valueOf("wrong_value");
    }

}
