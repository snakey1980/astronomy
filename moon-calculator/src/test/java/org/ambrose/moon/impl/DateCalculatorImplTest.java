package org.ambrose.moon.impl;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class DateCalculatorImplTest {

    private final DateCalculatorImpl dateCalculator = new DateCalculatorImpl();

    @Test
    public void testJulian() {
        assertEquals(LocalDateTime.of(2016, 12, 13, 0, 0), dateCalculator.toDateTime(2457735.5d));
        assertEquals(2457735.5d, dateCalculator.toJulian(LocalDateTime.of(2016, 12, 13, 0, 0)), 0.00000001d);
    }


}