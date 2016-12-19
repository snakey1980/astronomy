package org.ambrose.astronomy.moon;

import java.time.LocalDateTime;

public interface MoonCalculator {

    LocalDateTime timeOfPhase(double k);

    double approximateK(LocalDateTime dateTime);

}
