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

import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllVitalStatisticsTest extends SpringIntegrationTest {

    public static final String USER_NAME = "userName";
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

        allVitalStatistics.add(vitalStatistics_Today, USER_NAME);
        allVitalStatistics.add(vitalStatistics_Tomorrow, USER_NAME);
        allVitalStatistics.add(vitalStatistics_Yesterday, USER_NAME);

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
        allVitalStatistics.add(vitalStatistics, USER_NAME);

        vitalStatistics.setPulse(200);
        allVitalStatistics.update(vitalStatistics, USER_NAME);
        VitalStatistics savedVitalStatistics = allVitalStatistics.get(vitalStatistics.getId());

        assertEquals(new Integer(200), savedVitalStatistics.getPulse());
        assertEquals(vitalStatistics.getRevision(), savedVitalStatistics.getRevision());
    }

    @Test
    public void shouldReturnAllVitalStatisticsForAPatientBetweenADateRange() {
        LocalDate today = DateUtil.today();
        VitalStatistics vitalStatistics_10DaysAgo = new VitalStatisticsBuilder().withDefaults().withPatientId("patient_id").withCaptureDate(today.minusDays(10)).build();
        VitalStatistics vitalStatistics_Today = new VitalStatisticsBuilder().withDefaults().withPatientId("another_patient_id").withCaptureDate(today).build();
        VitalStatistics vitalStatistics_5DaysAfter = new VitalStatisticsBuilder().withDefaults().withPatientId("patient_id").withCaptureDate(today.plusDays(5)).build();
        VitalStatistics vitalStatistics_10DaysAfter = new VitalStatisticsBuilder().withDefaults().withPatientId("patient_id").withCaptureDate(today.plusDays(10)).build();

        allVitalStatistics.add(vitalStatistics_Today, USER_NAME);
        allVitalStatistics.add(vitalStatistics_5DaysAfter, USER_NAME);
        allVitalStatistics.add(vitalStatistics_10DaysAgo, USER_NAME);
        allVitalStatistics.add(vitalStatistics_10DaysAfter, USER_NAME);

        LocalDate startDate = today.minusDays(11);
        LocalDate endDate = today.plusDays(5);
        List<VitalStatistics> vitalStatistics = allVitalStatistics.findAllByPatientId("patient_id", startDate, endDate);

        assertEquals(2, vitalStatistics.size());
        assertEquals(vitalStatistics_10DaysAgo.getId(), vitalStatistics.get(0).getId());
        assertEquals(vitalStatistics_5DaysAfter.getId(), vitalStatistics.get(1).getId());
    }
}
