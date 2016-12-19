package org.ambrose.astronomy.sun.impl;

import org.ambrose.astronomy.dates.impl.DateCalculatorImpl;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SunCalculatorImplTest {

    private final SunCalculatorImpl sunCalculator = new SunCalculatorImpl(new DateCalculatorImpl());

    @Test
    public void test() {
        double expected = 199.908_95d;
        LocalDateTime dateTime = LocalDateTime.of(1992, 10, 13, 0, 0);
        double actual = sunCalculator.longitude(dateTime);
        assertEquals(expected, actual, 0.00001d);
    }

    @Test
    public void test2016() {
        for (LocalDateTime dateTime = LocalDateTime.of(2016, 1, 1, 0, 0); dateTime.isBefore(LocalDateTime.of(2017, 1, 1, 0, 0)); dateTime = dateTime.plusDays(1)) {
            System.out.println(dateTime + ": " + sunCalculator.longitude(dateTime));
        }
    }

    @Test
    public void testSeek() {
        for (int year = 1980; year < 2101; ++year) {
            for (int i = 0; i < 12; ++i) {
                double d = (300d + Double.valueOf(i) * 30d) % 360d;
                System.out.println(sunCalculator.seek(year, d));
            }
        }
    }




}