package org.motechproject.tama.patient.integration.repository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.*;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllTreatmentAdvicesTest extends SpringIntegrationTest {

    public static final String USER_NAME = "userName";

    @Autowired
    AllTreatmentAdvices allTreatmentAdvices;

    @Test
    public void shouldReturnNoTreatmentAdviceForAPatient() {
        String invalidPatientId = "999999";
        TreatmentAdvice retrievedTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(invalidPatientId);
        assertNull(retrievedTreatmentAdvice);
    }

    @Test
    public void shouldReturnCurrentTreatmentAdviceForAPatient() {
        TreatmentAdvice inactiveTreatmentAdvice = new TreatmentAdvice();
        TreatmentAdvice activeTreatmentAdvice = new TreatmentAdvice();
        inactiveTreatmentAdvice.setReasonForDiscontinuing("Bad Medicine");
        inactiveTreatmentAdvice.setPatientId("patientA");
        activeTreatmentAdvice.setPatientId("patientA");

        allTreatmentAdvices.add(activeTreatmentAdvice, USER_NAME);
        allTreatmentAdvices.add(inactiveTreatmentAdvice, USER_NAME);

        TreatmentAdvice retrievedTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice("patientA");

        assertNotNull(retrievedTreatmentAdvice);
        assertNull(retrievedTreatmentAdvice.getReasonForDiscontinuing());
        assertEquals("patientA", retrievedTreatmentAdvice.getPatientId());

        markForDeletion(inactiveTreatmentAdvice);
        markForDeletion(activeTreatmentAdvice);
    }

    @Test
    public void shouldReturnEarliestTreatmentAdvice() {
        LocalDate today = DateUtil.today();
        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBefore = yesterday.minusDays(1);

        TreatmentAdvice adviceStartingToday = TreatmentAdviceBuilder.startRecording().withDefaults().withPatientId("1111").withStartDate(today).build();
        TreatmentAdvice adviceStartingDayBefore = TreatmentAdviceBuilder.startRecording().withDefaults().withPatientId("1111").withStartDate(dayBefore).build();
        TreatmentAdvice adviceStartingYesterday = TreatmentAdviceBuilder.startRecording().withDefaults().withPatientId("1111").withStartDate(yesterday).build();

        allTreatmentAdvices.add(adviceStartingToday, USER_NAME);
        allTreatmentAdvices.add(adviceStartingDayBefore, USER_NAME);
        allTreatmentAdvices.add(adviceStartingYesterday, USER_NAME);

        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice("1111");

        assertNotNull(earliestTreatmentAdvice);
        assertEquals(dayBefore.toDate(), earliestTreatmentAdvice.getStartDate());

        markForDeletion(adviceStartingToday);
        markForDeletion(adviceStartingYesterday);
        markForDeletion(adviceStartingDayBefore);
    }
}
