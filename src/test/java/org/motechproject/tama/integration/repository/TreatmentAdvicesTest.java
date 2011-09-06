package org.motechproject.tama.integration.repository;

import org.junit.Test;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;

public class TreatmentAdvicesTest extends SpringIntegrationTest {

    @Autowired
    AllTreatmentAdvices allTreatmentAdvices;

    @Test
    public void testFindByPatientId() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("111111");
        allTreatmentAdvices.add(treatmentAdvice);

        TreatmentAdvice retrievedTreatmentAdvice = allTreatmentAdvices.findByPatientId("111111");

        assertNotNull(retrievedTreatmentAdvice);
        assertEquals("111111", retrievedTreatmentAdvice.getPatientId());

        markForDeletion(treatmentAdvice);
    }

    @Test
    public void testFindByPatientIdReturnsNoTreatmentAdvice() {
        String invalidPatientId = "999999";
        TreatmentAdvice retrievedTreatmentAdvice = allTreatmentAdvices.findByPatientId(invalidPatientId);
        assertNull(retrievedTreatmentAdvice);
    }

    @Test
    public void shouldReturnTreatmentAdviceWhichIsInProgress(){
        TreatmentAdvice inactiveTreatmentAdvice = new TreatmentAdvice();
        TreatmentAdvice activeTreatmentAdvice = new TreatmentAdvice();
        inactiveTreatmentAdvice.setReasonForDiscontinuing("Bad Medicine");
        inactiveTreatmentAdvice.setPatientId("patientA");
        activeTreatmentAdvice.setPatientId("patientA");

        allTreatmentAdvices.add(activeTreatmentAdvice);
        allTreatmentAdvices.add(inactiveTreatmentAdvice);

        TreatmentAdvice retrievedTreatmentAdvice = allTreatmentAdvices.findByPatientId("patientA");

        assertNotNull(retrievedTreatmentAdvice);
        assertNull(retrievedTreatmentAdvice.getReasonForDiscontinuing());
        assertEquals("patientA", retrievedTreatmentAdvice.getPatientId());

        markForDeletion(inactiveTreatmentAdvice);
        markForDeletion(activeTreatmentAdvice);
    }
}
