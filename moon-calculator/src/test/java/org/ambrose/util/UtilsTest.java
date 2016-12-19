package org.ambrose.util;

import org.junit.Test;

import static org.ambrose.util.Utils.*;
import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testWind() {
        assertEquals(0.5d, wind(0.5d,1d), 0.00001);
        assertEquals(0.5d, wind(1.5d,1d), 0.00001);
        assertEquals(0.5d, wind(-3.5d,1d), 0.00001);
        assertEquals(5d, wind(365d,360d), 0.00001);
        assertEquals(1d, wind(721d,360d), 0.00001);
        assertEquals(1d, wind(-359d,360d), 0.00001);
    }

}