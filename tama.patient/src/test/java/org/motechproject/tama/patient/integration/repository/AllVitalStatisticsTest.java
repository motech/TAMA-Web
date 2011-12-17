package org.motechproject.tama.patient.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllVitalStatisticsTest extends SpringIntegrationTest {

    @Autowired
    private AllVitalStatistics allVitalStatistics;

    @Test
    public void shouldFindVitalStatisticsByPatientId() {
        VitalStatistics vitalStatistics = new VitalStatistics();
        vitalStatistics.setPatientId("patient_id");
        allVitalStatistics.add(vitalStatistics);

        assertEquals("patient_id", allVitalStatistics.findByPatientId("patient_id").getPatientId());
        markForDeletion(vitalStatistics);
    }

    @Test
    public void shouldUpdateVitalStatisticsPreservingTheRevision() {
        VitalStatistics vitalStatistics = new VitalStatistics();
        vitalStatistics.setPatientId("patient_id");
        vitalStatistics.setPulse(100);
        allVitalStatistics.add(vitalStatistics);

        vitalStatistics.setPulse(200);
        allVitalStatistics.update(vitalStatistics);

        VitalStatistics savedVitalStatistics = allVitalStatistics.get(vitalStatistics.getId());
        assertEquals(new Integer(200), savedVitalStatistics.getPulse());
        assertEquals(vitalStatistics.getRevision(), savedVitalStatistics.getRevision());
    }
}
