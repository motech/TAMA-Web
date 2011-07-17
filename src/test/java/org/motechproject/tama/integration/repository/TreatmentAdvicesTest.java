package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;

public class TreatmentAdvicesTest extends SpringIntegrationTest {

    @Autowired
    TreatmentAdvices treatmentAdvices;

    @Test
    public void testFindByPatientId() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("111111");
        treatmentAdvices.add(treatmentAdvice);

        TreatmentAdvice retrievedTreatmentAdvice = treatmentAdvices.findByPatientId("111111");

        Assert.assertNotNull(retrievedTreatmentAdvice);
        Assert.assertEquals("111111", retrievedTreatmentAdvice.getPatientId());

        markForDeletion(treatmentAdvice);
    }

    @Test
    public void testFindByPatientIdReturnsNoTreatmentAdvice() {
        String invalidPatientId = "999999";
        TreatmentAdvice retrievedTreatmentAdvice = treatmentAdvices.findByPatientId(invalidPatientId);
        Assert.assertNull(retrievedTreatmentAdvice);
    }
}
