package org.motechproject.tama.integration.repository;

import org.junit.Test;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;

public class TreatmentAdvicesTest extends SpringIntegrationTest {

    @Autowired
    TreatmentAdvices treatmentAdvices;

    @Test
    public void testFindByPatientId() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("111111");
        treatmentAdvices.add(treatmentAdvice);

        TreatmentAdvice retrievedTreatmentAdvice = treatmentAdvices.findByPatientId("111111");

        assertNotNull(retrievedTreatmentAdvice);
        assertEquals("111111", retrievedTreatmentAdvice.getPatientId());

        markForDeletion(treatmentAdvice);
    }

    @Test
    public void testFindByPatientIdReturnsNoTreatmentAdvice() {
        String invalidPatientId = "999999";
        TreatmentAdvice retrievedTreatmentAdvice = treatmentAdvices.findByPatientId(invalidPatientId);
        assertNull(retrievedTreatmentAdvice);
    }

    @Test
    public void shouldReturnTreatmentAdviceWhichIsInProgress(){
        TreatmentAdvice inactiveTreatmentAdvice = new TreatmentAdvice();
        TreatmentAdvice activeTreatmentAdvice = new TreatmentAdvice();
        inactiveTreatmentAdvice.setReasonForDiscontinuing("Bad Medicine");
        inactiveTreatmentAdvice.setPatientId("patientA");
        activeTreatmentAdvice.setPatientId("patientA");

        treatmentAdvices.add(activeTreatmentAdvice);
        treatmentAdvices.add(inactiveTreatmentAdvice);

        TreatmentAdvice retrievedTreatmentAdvice = treatmentAdvices.findByPatientId("patientA");

        assertNotNull(retrievedTreatmentAdvice);
        assertNull(retrievedTreatmentAdvice.getReasonForDiscontinuing());
        assertEquals("patientA", retrievedTreatmentAdvice.getPatientId());

        markForDeletion(inactiveTreatmentAdvice);
        markForDeletion(activeTreatmentAdvice);
    }
}
