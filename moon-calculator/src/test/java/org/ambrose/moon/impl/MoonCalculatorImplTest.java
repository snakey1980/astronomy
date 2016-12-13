package org.ambrose.moon.impl;

import org.ambrose.moon.MoonCalculator;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class MoonCalculatorImplTest {

    @Test
    public void test() {
        MoonCalculator moonCalculator = new MoonCalculatorImpl(new DateCalculatorImpl());
        assertEquals(LocalDateTime.of(2000, 1, 6, 18, 14), moonCalculator.timeOfPhase(0d));
        assertEquals(LocalDateTime.of(2000, 1, 14, 13, 34), moonCalculator.timeOfPhase(0.25d));
        assertEquals(LocalDateTime.of(2000, 1, 21, 4, 40), moonCalculator.timeOfPhase(0.5d));
        assertEquals(LocalDateTime.of(2000, 1, 28, 7, 57), moonCalculator.timeOfPhase(0.75d));
    }

}