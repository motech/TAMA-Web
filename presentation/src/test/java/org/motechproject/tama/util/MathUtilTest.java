package org.motechproject.tama.util;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class MathUtilTest {
    @Test
    public void roundOffANumber_TwoDecimalPlaces() {
        assertEquals(2.11, MathUtil.roundOffTo(2.111, 2));
        assertEquals(2.16, MathUtil.roundOffTo(2.156, 2));
    }

    @Test
    public void roundOffANumber_ThreeDecimalPlaces() {
        assertEquals(2.145, MathUtil.roundOffTo(2.1452, 3));
        assertEquals(2.157, MathUtil.roundOffTo(2.1567, 3));
    }
}
