package org.motechproject.tama.patient.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.util.DateUtil;
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
        vitalStatistics.setCaptureDate(DateUtil.today());
        allVitalStatistics.add(vitalStatistics);
        markForDeletion(vitalStatistics);

        assertEquals("patient_id", allVitalStatistics.findLatestVitalStatisticByPatientId("patient_id").getPatientId());
    }

    @Test
    public void shouldUpdateVitalStatisticsPreservingTheRevision() {
        VitalStatistics vitalStatistics = new VitalStatistics();
        vitalStatistics.setPatientId("patient_id");
        vitalStatistics.setCaptureDate(DateUtil.today());
        vitalStatistics.setPulse(100);
        allVitalStatistics.add(vitalStatistics);

        vitalStatistics.setPulse(200);
        allVitalStatistics.update(vitalStatistics);
        VitalStatistics savedVitalStatistics = allVitalStatistics.get(vitalStatistics.getId());
        markForDeletion(vitalStatistics);
        
        assertEquals(new Integer(200), savedVitalStatistics.getPulse());
        assertEquals(vitalStatistics.getRevision(), savedVitalStatistics.getRevision());
    }
}
