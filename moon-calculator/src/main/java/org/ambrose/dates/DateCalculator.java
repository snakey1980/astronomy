package org.ambrose.dates;

import java.time.LocalDateTime;

public interface DateCalculator {

    LocalDateTime toDateTime(double julian);

    double toJulian(LocalDateTime dateTime);

    LocalDateTime toCivilTime(LocalDateTime dynamicalTime);

    LocalDateTime toNearestMinute(LocalDateTime dateTime);

}
