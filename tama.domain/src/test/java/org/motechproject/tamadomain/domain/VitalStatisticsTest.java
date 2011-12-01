package org.motechproject.tamadomain.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class VitalStatisticsTest {
    @Test
    public void shouldCalculateBMI_givenHeightAndWeight_InTheMetricSystem() {
        VitalStatistics vitalStatistics = new VitalStatistics();
        vitalStatistics.setHeightInCm(174.00);
        vitalStatistics.setWeightInKg(74.00);

        assertEquals(24.44, vitalStatistics.getBMI());
    }
}
