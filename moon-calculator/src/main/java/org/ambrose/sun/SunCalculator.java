package org.ambrose.sun;

import java.time.LocalDateTime;

public interface SunCalculator {

    double longitude(LocalDateTime dateTime);

    LocalDateTime seek(int year, double longitude);

}
