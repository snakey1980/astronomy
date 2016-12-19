# MoonCalculator
Moon phase calculator based on the method described by Jean Meeus in Astronomical Algorithms (https://www.amazon.com/Astronomical-Algorithms-Jean-Meeus/dp/0943396352).

The interface is:

    LocalDateTime timeOfPhase(double k);
  
An integer value of k gives a new moon, an integer plus 0.25 gives a first quarter, plus 0.5 gives a full moon and plus 0.75 gives a last quarter.  k = 0 gives the new moon of January 6, 2000.  So, for example, k = 0.5 gives the following full moon, and k = 10.5 gives the full moon ten iterations later.  Negative values of k are accepted and give phases before 2000.  There is also the method

    double approximateK(LocalDateTime dateTime);

which gives an approximate value of k for a given date.

It will not give an answer for any dates before 1800.

# SunCalculator
Sun longitude calculator based on Jean Meeus.  The interface is:

    double longitude(LocalDateTime dateTime);

which gives the apparent longitude in degrees at the given time.  There is also the method

    LocalDateTime seek(int year, double longitude);

which finds, to the nearest minute, the moment during the given year when the given longitude was apparent.  This only accepts multiple of 30 degrees in [0, 360).
