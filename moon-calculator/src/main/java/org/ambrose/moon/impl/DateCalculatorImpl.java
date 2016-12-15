package org.ambrose.moon.impl;

import org.ambrose.moon.DateCalculator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateCalculatorImpl implements DateCalculator {

    private static final LocalDateTime JAN_1_1600_MIDNIGHT_DATETIME = LocalDateTime.of(1600, 1, 1, 0, 0);
    private static final double JAN_1_1600_MIDNIGNT_JULIAN = 2_305_447.5d;
    private static final long SECONDS_IN_DAY = 60 * 60 * 24;

    @Override
    public LocalDateTime toDateTime(double julian) {
        if (julian < JAN_1_1600_MIDNIGNT_JULIAN) {
            throw new IllegalArgumentException();
        }
        double diff = julian - JAN_1_1600_MIDNIGNT_JULIAN;
        long daysDiff = (long) diff;
        long partDaySeconds = (long) ((diff - Double.valueOf(daysDiff)) * Double.valueOf(SECONDS_IN_DAY));
        return JAN_1_1600_MIDNIGHT_DATETIME.plusDays(daysDiff).plusSeconds(partDaySeconds);
    }

    @Override
    public double toJulian(LocalDateTime dateTime) {
        if (dateTime.isBefore(JAN_1_1600_MIDNIGHT_DATETIME)) {
            throw new IllegalArgumentException();
        }
        long secondsSinceStart = Duration.between(JAN_1_1600_MIDNIGHT_DATETIME, dateTime).getSeconds();
        long daysSinceStart = secondsSinceStart / SECONDS_IN_DAY;
        double partDayFraction = Double.valueOf(secondsSinceStart % SECONDS_IN_DAY) / Double.valueOf(SECONDS_IN_DAY);
        return JAN_1_1600_MIDNIGNT_JULIAN + Double.valueOf(daysSinceStart) + partDayFraction;
    }

    @Override
    public LocalDateTime toCivilTime(LocalDateTime dynamicalTime) {
        double delta = 0;
        if (dynamicalTime.getYear() < 1800) {
            throw new IllegalArgumentException();
        }
        if (dynamicalTime.getYear() < 1998) {
            long secondsSince1900 = Duration.between(LocalDateTime.of(1900, 1, 1, 0, 0), dynamicalTime.truncatedTo(ChronoUnit.DAYS)).get(ChronoUnit.SECONDS);
            long daysSince1900 = secondsSince1900 / SECONDS_IN_DAY;
            double theta = Double.valueOf(daysSince1900) / 36525d;
            if (dynamicalTime.getYear() < 1900) {
                delta = - 2.5d + 228.95d * theta + 5_218.61d * Math.pow(theta, 2) + 56_282.84d * Math.pow(theta, 3) + 324_011.78d * Math.pow(theta, 4)
                        + 1_061_660.75d * Math.pow(theta, 5) + 2_087_298.89d * Math.pow(theta, 6) + 2_513_807.78d * Math.pow(theta, 7)
                        + 1_818_961.41d * Math.pow(theta, 8) + 727_058.63 * Math.pow(theta, 9) + 123_563.95 * Math.pow(theta, 10);
            }
            else {
                delta = - 2.44d + 87.24d * theta + 815.2d * Math.pow(theta, 2) - 2_637.8d * Math.pow(theta, 3) - 18_756.33d * Math.pow(theta, 4)
                        + 124_906.15d * Math.pow(theta, 5) - 303_191.19d * Math.pow(theta, 6) + 372_919.88d * Math.pow(theta, 7)
                        - 232_424.66d * Math.pow(theta, 8) + 58_353.42 * Math.pow(theta, 9);
            }
        }
        else {
            if (dynamicalTime.getYear() < 2008) {
                delta = new double[]{
                        63.00d, // 1998
                        63.40d, // 1999
                        63.83d, // 2000
                        64.09d, // 2001
                        64.30d, // 2002
                        64.47d, // 2003
                        64.57d, // 2004
                        64.69d, // 2005
                        64.85d, // 2006
                        65.15d, // 2007
                }[dynamicalTime.getYear() - 1998];
            }
            else { // year >= 2008
                delta = 66d;
            }
        }
        return dynamicalTime.minusSeconds((long) delta);
    }

    @Override
    public LocalDateTime toNearestMinute(LocalDateTime dateTime) {
        LocalDateTime truncated = dateTime.truncatedTo(ChronoUnit.MINUTES);
        if (dateTime.getSecond() < 30) {
            return truncated;
        }
        else {
            return truncated.plusMinutes(1);
        }
    }
}
