package org.ambrose.moon.impl;

import org.ambrose.moon.MoonCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MoonCalculatorImplTest {

    private final MoonCalculatorImpl moonCalculator = new MoonCalculatorImpl(new DateCalculatorImpl());

    @Test
    public void testWind() {
        assertEquals(0.5d, moonCalculator.wind(0.5d,1d), 0.00001);
        assertEquals(0.5d, moonCalculator.wind(1.5d,1d), 0.00001);
        assertEquals(0.5d, moonCalculator.wind(-3.5d,1d), 0.00001);
        assertEquals(5d, moonCalculator.wind(365d,360d), 0.00001);
        assertEquals(1d, moonCalculator.wind(721d,360d), 0.00001);
        assertEquals(1d, moonCalculator.wind(-359d,360d), 0.00001);
    }

    @Test
    public void testWithin2MinutesOfNavy() throws IOException {
        Map<Double, String> phaseMap = new HashMap<>();
        phaseMap.put(0d, "New Moon");
        phaseMap.put(0.25d, "First Quarter");
        phaseMap.put(0.5d, "Full Moon");
        phaseMap.put(0.75d, "Last Quarter");
        long maxDiff = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/navy.csv")));
        for (double k = -2473.75d; k <= 1236.5d; k = k + 0.25d) {
            String row = reader.readLine();
            String[] bits = row.split(",");
            String phase = bits[0];
            double phaseType = k;
            while (phaseType >= 1) {
                phaseType = phaseType - 1d;
            }
            while (phaseType < 0) {
                phaseType = phaseType + 1d;
            }
            assertEquals(phaseMap.get(phaseType), phase);
            LocalDateTime navyTime = LocalDateTime.parse(bits[1] + bits[2], DateTimeFormatter.ofPattern("yyyy MMM ddHH:mm"));
            LocalDateTime ourTime = moonCalculator.timeOfPhase(k);
            long diff = Duration.between(navyTime, ourTime).abs().getSeconds() / 60;
            if (diff > maxDiff) {
                maxDiff = diff;
            }
        }
        reader.close();
        assertTrue(maxDiff <= 2);
    }

}