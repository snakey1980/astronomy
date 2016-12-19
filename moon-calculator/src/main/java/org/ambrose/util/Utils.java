package org.ambrose.util;

public class Utils {

    public static double toRadians(double degrees) {
        return Math.toRadians(wind(degrees, 360));
    }

    /**
     * Wind the given number into the interval [0, intervalEndExclusive)
     * @param d
     * @param intervalEndExclusive
     * @return
     */
    public static double wind(double d, double intervalEndExclusive) {
        return d - intervalEndExclusive * (Math.floor(d / intervalEndExclusive));
    }
}
