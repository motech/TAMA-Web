package org.motechproject.tama.patient.integration.repository;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.VitalStatisticsBuilder;
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

    @After
    public void tearDown(){
        markForDeletion(allVitalStatistics.getAll().toArray());
    }

    @Test
    public void shouldFindLatestVitalStatisticsByPatientId() {
        LocalDate today = DateUtil.today();
        VitalStatistics vitalStatistics_Yesterday = new VitalStatisticsBuilder().withDefaults().withCaptureDate(today.minusDays(1)).withPatientId("patient_id").build();
        VitalStatistics vitalStatistics_Tomorrow = new VitalStatisticsBuilder().withDefaults().withCaptureDate(today.plusDays(1)).withPatientId("patient_id").build();
        VitalStatistics vitalStatistics_Today = new VitalStatisticsBuilder().withDefaults().withCaptureDate(today).withPatientId("patient_id").build();

        allVitalStatistics.add(vitalStatistics_Today);
        allVitalStatistics.add(vitalStatistics_Tomorrow);
        allVitalStatistics.add(vitalStatistics_Yesterday);

        VitalStatistics latestVitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId("patient_id");
        assertEquals("patient_id", latestVitalStatistics.getPatientId());
        assertEquals(vitalStatistics_Tomorrow.getId(), latestVitalStatistics.getId());
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

        assertEquals(new Integer(200), savedVitalStatistics.getPulse());
        assertEquals(vitalStatistics.getRevision(), savedVitalStatistics.getRevision());
    }
}
