package org.ambrose.astronomy.moon.impl;

import org.ambrose.astronomy.dates.DateCalculator;
import org.ambrose.astronomy.dates.impl.DateCalculatorImpl;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoonCalculatorImplTest {

    private final MoonCalculatorImpl moonCalculator = new MoonCalculatorImpl(new DateCalculatorImpl());

    @Test
    public void testApproximateK() {
        // last quarter near beginning of 2044
        double k = moonCalculator.approximateK(LocalDateTime.of(2044, 1, 1, 0, 0));
        assertEquals(544.25d, k, 0.00000001d);
        while (moonCalculator.phaseType(k) != MoonCalculatorImpl.LAST_QUARTER) {
            k = k + 0.25d;
        }
        DateCalculator dateCalculator = new DateCalculatorImpl();
        LocalDateTime actual = dateCalculator.toNearestMinute(dateCalculator.toDateTime(moonCalculator.truePhaseTimeJulian(k)));
        assertEquals(LocalDateTime.of(2044, 1, 21, 23, 48), actual);
    }

    @Test
    public void testWithin1MinuteOfNavy() throws IOException {
        Map<Double, String> phaseMap = new HashMap<>();
        phaseMap.put(MoonCalculatorImpl.NEW, "New Moon");
        phaseMap.put(MoonCalculatorImpl.FIRST_QUARTER, "First Quarter");
        phaseMap.put(MoonCalculatorImpl.FULL, "Full Moon");
        phaseMap.put(MoonCalculatorImpl.LAST_QUARTER, "Last Quarter");
        long maxDiff = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/navy.csv")));
        for (double k = -2473.75d; k <= 1236.5d; k = k + 0.25d) {
            String row = reader.readLine();
            String[] bits = row.split(",");
            String phase = bits[0];
            double phaseType = moonCalculator.phaseType(k);
            assertEquals(phaseMap.get(phaseType), phase);
            LocalDateTime navyTime = LocalDateTime.parse(bits[1] + bits[2], DateTimeFormatter.ofPattern("yyyy MMM ddHH:mm"));
            LocalDateTime ourTime = moonCalculator.timeOfPhase(k);
            long diff = Duration.between(navyTime, ourTime).abs().getSeconds() / 60;
            if (diff > 0) {
                System.err.println(diff + " " + ourTime);
            }
            if (diff > maxDiff) {
                maxDiff = diff;
            }
        }
        reader.close();
        assertTrue(maxDiff <= 1);
    }

}