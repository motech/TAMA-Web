package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.domain.VitalStatistics;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

public class VitalStatisticsUIModelTest {

    private VitalStatisticsUIModel vitalStatisticsUIModel;

    @Before
    public void setUp() {
        vitalStatisticsUIModel = new VitalStatisticsUIModel();
    }

    @Test
    public void shouldNotSetIdWhenItIsEmpty() {
        vitalStatisticsUIModel.setId("");
        assertNull(vitalStatisticsUIModel.getId());
    }

    @Test
    public void shouldNotSetIdWhenItIsNull() {
        VitalStatistics vitalStatistics = mock(VitalStatistics.class);
        vitalStatisticsUIModel.setVitalStatistics(vitalStatistics);

        vitalStatisticsUIModel.setId(null);
        verify(vitalStatistics, never()).setId(null);
    }

    @Test
    public void shouldSetIdWhenItIsNotEmpty() {
        vitalStatisticsUIModel.setId("id");
        assertEquals("id", vitalStatisticsUIModel.getId());
    }
}
