# moon
Moon phase calculator based on the method described by Jean Meeus in Astronomical Algorithms (https://www.amazon.com/Astronomical-Algorithms-Jean-Meeus/dp/0943396352).

The interface is:

    LocalDateTime timeOfPhase(double k);
  
An integer value of k gives a new moon, an integer plus 0.25 gives a first quarter, plus 0.5 gives a full moon and plus 0.75 gives a last quarter.  k = 0 gives the new moon of January 6, 2000.  So, for example, k = 0.5 gives the following full moon, and k = 10.5 gives the full moon ten iterations later.  Negative values of k are accepted and give phases before 2000.

The implementation here will not give an answer for any dates before 1800.
