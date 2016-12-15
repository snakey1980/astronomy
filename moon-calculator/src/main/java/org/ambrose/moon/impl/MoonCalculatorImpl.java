package org.ambrose.moon.impl;

import org.ambrose.moon.DateCalculator;
import org.ambrose.moon.MoonCalculator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

public class MoonCalculatorImpl implements MoonCalculator {

    static final double NEW = 0d;
    static final double FIRST_QUARTER = 0.25d;
    static final double FULL = 0.5d;
    static final double LAST_QUARTER = 0.75d;

    static final List<Double> PHASE_TYPES = asList(
            NEW,
            FIRST_QUARTER,
            FULL,
            LAST_QUARTER
    );

    private final DateCalculator dateCalculator;

    public MoonCalculatorImpl(DateCalculator dateCalculator) {
        this.dateCalculator = dateCalculator;
    }

    public LocalDateTime timeOfPhase(double k) {
        return dateCalculator.toNearestMinute(dateCalculator.toCivilTime(dateCalculator.toDateTime(truePhaseTimeJulian(k))));
    }

    @Override
    public double approximateK(LocalDateTime dateTime) {
        LocalDateTime beginningOfYear = dateTime.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime beginningOfNextYear = beginningOfYear.plusYears(1);
        double secondsInYear = Duration.between(beginningOfYear, beginningOfNextYear).getSeconds();
        double secondsToDateTime = Duration.between(beginningOfYear, dateTime).getSeconds();
        double yearFraction = secondsToDateTime / secondsInYear;
        double year = Double.valueOf(dateTime.getYear()) + yearFraction;
        double approximation = (year - 2000d) * 12.3685d;
        return Math.round(approximation * 4d) / 4d;
    }

    double phaseType(double k) {
        return wind(k, 1);
    }

    double truePhaseTimeJulian(double k) {
        double phaseType = phaseType(k);
        if (!PHASE_TYPES.contains(phaseType)) {
            throw new IllegalArgumentException();
        }
        double t = k / 1_236.85d; // time in Julian centuries since the epoch 2000
        double meanPhaseTime = 2_451_550.097_66d + 29.530_588_861d * k
                + 0.000_154_37d * t * t
                - 0.000_000_150d * t * t * t
                + 0.000_000_000_73d * t * t * t * t;
        double e = 1 - 0.002_516d * t - 0.000_007_4d * t * t; // eccentricity decrease correction
        double m = toRadians(2.553_4d + 29.105_356_70d * k // sun's mean anomaly
                - 0.000_001_4d * t * t
                - 0.000_000_11d * t * t * t);
        double m_prime = toRadians(201.564_3d + 385.816_935_28d * k // moon's mean anomaly
                + 0.010_758_2d * t * t
                + 0.000_012_38d * t * t * t
                - 0.000_000_058d * t * t * t * t);
        double f = toRadians(160.710_8d + 390.670_502_84d * k // moon's argument of latitude
                - 0.001_611_8d * t * t
                - 0.000_002_27d * t * t * t
                + 0.000_000_011d * t * t * t * t);
        double omega = toRadians(124.774_6d - 1.563_755_88d * k // longitude of the ascending node of the lunar orbit
                + 0.002_067_2d * t * t
                + 0.000_002_15d * t * t * t);
        // planetary arguments:
        double aAdjustment = 0.009_173d * t * t;
        double a1 =  toRadians(299.77d +  0.107_408d * k - aAdjustment);
        double a2 =  toRadians(251.88d +  0.016_321d * k - aAdjustment);
        double a3 =  toRadians(251.83d + 26.651_886d * k - aAdjustment);
        double a4 =  toRadians(349.42d + 36.412_478d * k - aAdjustment);
        double a5 =  toRadians( 84.66d + 18.206_239d * k - aAdjustment);
        double a6 =  toRadians(141.74d + 53.303_771d * k - aAdjustment);
        double a7 =  toRadians(207.14d +  2.453_732d * k - aAdjustment);
        double a8 =  toRadians(154.84d +  7.306_860d * k - aAdjustment);
        double a9 =  toRadians( 34.52d + 27.261_239d * k - aAdjustment);
        double a10 = toRadians(207.19d +  0.121_824d * k - aAdjustment);
        double a11 = toRadians(291.34d +  1.844_379d * k - aAdjustment);
        double a12 = toRadians(161.72d + 24.198_154d * k - aAdjustment);
        double a13 = toRadians(239.56d + 25.513_099d * k - aAdjustment);
        double a14 = toRadians(331.55d +  3.592_518d * k - aAdjustment);
        double correction = 0d;
        if (phaseType == 0d || phaseType == 0.5d) { // new or full moon
            boolean new_ = phaseType == 0d;
            correction =
                            - (new_ ? 0.407_20d : 0.406_14d)         * Math.sin(m_prime)
                            + (new_ ? 0.172_41d : 0.173_02d) * e     * Math.sin(m)
                            + (new_ ? 0.016_08d : 0.016_14d)         * Math.sin(2d * m_prime)
                            + (new_ ? 0.010_39d : 0.010_43d)         * Math.sin(2d * f)
                            + (new_ ? 0.007_39d : 0.007_34d) * e     * Math.sin(m_prime - m)
                            - (new_ ? 0.005_14d : 0.005_15d) * e     * Math.sin(m_prime + m)
                            + (new_ ? 0.002_08d : 0.002_09d) * e * e * Math.sin(2d * m)
                            - (       0.001_11d            )         * Math.sin(m_prime - 2d * f)
                            - (       0.000_57d            )         * Math.sin(m_prime + 2d * f)
                            + (       0.000_56d            ) * e     * Math.sin(2d * m_prime + m)
                            - (       0.000_42d            )         * Math.sin(3d * m_prime)
                            + (       0.000_42d            ) * e     * Math.sin(m + 2d * f)
                            + (       0.000_38d            ) * e     * Math.sin(m - 2d * f)
                            - (       0.000_24d            ) * e     * Math.sin(2d * m_prime - m)
                            - (       0.000_17d            )         * Math.sin(omega)
                            - (       0.000_07d            )         * Math.sin(m_prime + 2d * m)
                            + (       0.000_04d            )         * Math.sin(2d * m_prime - 2d * f)
                            + (       0.000_04d            )         * Math.sin(3d * m)
                            + (       0.000_03d            )         * Math.sin(m_prime + m - 2d * f)
                            + (       0.000_03d            )         * Math.sin(2d * m_prime + 2d * f)
                            - (       0.000_03d            )         * Math.sin(m_prime + m + 2d * f)
                            + (       0.000_03d            )         * Math.sin(m_prime - m + 2d * f)
                            - (       0.000_02d            )         * Math.sin(m_prime - m - 2d * f)
                            - (       0.000_02d            )         * Math.sin(3d * m_prime + m)
                            + (       0.000_02d            )         * Math.sin(4d * m_prime);
        }
        else { // first and last quarter
            correction =
                            - 0.628_01d         * Math.sin(m_prime)
                            + 0.171_72d * e     * Math.sin(m)
                            - 0.011_83d * e     * Math.sin(m_prime + m)
                            + 0.008_62d         * Math.sin(2d * m_prime)
                            + 0.008_04d         * Math.sin(2d * f)
                            + 0.004_54d * e     * Math.sin(m_prime - m)
                            + 0.002_04d * e * e * Math.sin(2d * m)
                            - 0.001_80d         * Math.sin(m_prime - 2d * f)
                            - 0.000_70d         * Math.sin(m_prime + 2d * f)
                            - 0.000_40d         * Math.sin(3d * m_prime)
                            - 0.000_34d * e     * Math.sin(2d * m_prime - m)
                            + 0.000_32d * e     * Math.sin(m + 2d * f)
                            + 0.000_32d * e     * Math.sin(m - 2d * f)
                            - 0.000_28d * e * e * Math.sin(m_prime + 2d * m)
                            + 0.000_27d * e     * Math.sin(2d * m_prime + m)
                            - 0.000_17d         * Math.sin(omega)
                            - 0.000_05d         * Math.sin(m_prime - m - 2d * f)
                            + 0.000_04d         * Math.sin(2d * m_prime + 2d * f)
                            - 0.000_04d         * Math.sin(m_prime + m + 2d * f)
                            + 0.000_04d         * Math.sin(m_prime - 2d * m)
                            + 0.000_03d         * Math.sin(m_prime + m - 2d * f)
                            + 0.000_03d         * Math.sin(3d * m)
                            + 0.000_02d         * Math.sin(2d * m_prime - 2d * f)
                            + 0.000_02d         * Math.sin(m_prime - m + 2d * f)
                            - 0.000_02d         * Math.sin(3d * m_prime + m);
            double w = 0.003_06d - 0.000_38d * e * Math.cos(m) + 0.000_26d * Math.cos(m_prime)
                    - 0.000_02d * Math.cos(m_prime - m) + 0.000_02d * Math.cos(m_prime + m) + 0.000_02d * Math.cos(2d * f);
            if (phaseType == 0.25d) { // first quarter
                correction = correction + w;
            }
            else { // last quarter
                correction = correction - w;
            }
        }
        correction = correction
                + 0.000_325d * Math.sin(a1)
                + 0.000_165d * Math.sin(a2)
                + 0.000_164d * Math.sin(a3)
                + 0.000_126d * Math.sin(a4)
                + 0.000_110d * Math.sin(a5)
                + 0.000_062d * Math.sin(a6)
                + 0.000_060d * Math.sin(a7)
                + 0.000_056d * Math.sin(a8)
                + 0.000_047d * Math.sin(a9)
                + 0.000_042d * Math.sin(a10)
                + 0.000_040d * Math.sin(a11)
                + 0.000_037d * Math.sin(a12)
                + 0.000_035d * Math.sin(a13)
                + 0.000_023d * Math.sin(a14);
        return meanPhaseTime + correction;
    }

    private double toRadians(double degrees) {
        return Math.toRadians(wind(degrees, 360));
    }

    /**
     * Wind the given number into the interval [0, intervalEndExclusive)
     * @param d
     * @param intervalEndExclusive
     * @return
     */
    double wind(double d, double intervalEndExclusive) {
        return d - intervalEndExclusive * (Math.floor(d / intervalEndExclusive));
    }

}
