package org.ambrose.astronomy.sun.impl;

import org.ambrose.astronomy.dates.DateCalculator;
import org.ambrose.astronomy.sun.SunCalculator;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static org.ambrose.astronomy.util.Utils.toRadians;
import static org.ambrose.astronomy.util.Utils.wind;

public class SunCalculatorImpl implements SunCalculator {

    private final DateCalculator dateCalculator;

    public SunCalculatorImpl(DateCalculator dateCalculator) {
        this.dateCalculator = dateCalculator;
    }

    @Override
    public double longitude(LocalDateTime dateTime) {
        double t = (dateCalculator.toJulian(dateTime) - 2_451_545d) / 36525d; // Julian centuries since J2000
        double l0 = 280.466_46d + 36_000.769_83d * t + 0.000_303_2d * t * t; // geometric mean longitude
        double m = 357.529_11d + 35_999.050_29d * t - 0.000_153_7d * t * t; // sun's mean anomaly
        double c = (1.914_602d - 0.004_817d * t - 0.000_014d * t * t) * Math.sin(toRadians(m)) // sun's equation of the center
                + (0.019_993d - 0.000_101d * t) * Math.sin(toRadians(2 * m))
                + 0.000_289d * Math.sin(toRadians(3 * m));
        double dotCircle = l0 + c;
        double omega = 125.04d - 1_934.136d * t;
        double lambda = dotCircle - 0.005_69d - 0.004_78d * Math.sin(toRadians(omega));
        return wind(lambda, 360);
    }

    @Override
    public LocalDateTime seek(int year, double key) {
        if (key < 0d || key >= 360d) {
            throw new IllegalArgumentException();
        }
        if (key % 30d != 0) {
            throw new IllegalArgumentException();
        }
        int month = (int) Math.round(key / 30d) + 3;
        if (month > 12) {
            month = month - 12;
        }
        LocalDateTime low = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime high = low.plusMonths(1).minusSeconds(1);
        boolean discontinuity = longitude(high) < longitude(low);
        LocalDateTime mid = low;
        while (!low.isAfter(high)) {
            mid = LocalDateTime.ofEpochSecond(
                    (low.atOffset(UTC).toEpochSecond() + high.atOffset(UTC).toEpochSecond()) / 2l,
                    0,
                    UTC);
            double midVal = longitude(mid);
            if (discontinuity && midVal > 180d) {
                midVal = midVal - 360d;
            }
            if (midVal < key) {
                low = mid.plusSeconds(1);
            }
            else if (midVal > key) {
                high = mid.minusSeconds(1);
            }
            else {
                break;
            }
        }
        return dateCalculator.toNearestMinute(mid);
    }


}
