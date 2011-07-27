package org.motechproject.tama.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class NonHIVMedicalHistoryTest{

    @Test
    public void shouldGetSelectedAllergies() {
        NonHIVMedicalHistory nonHIVMedicalHistory = new NonHIVMedicalHistory();
        ArrayList<AllergyHistory> allergiesHistory = getAllergiesHistory();

        nonHIVMedicalHistory.setAllergiesHistory(allergiesHistory);
        Assert.assertEquals(1, nonHIVMedicalHistory.getSpecifiedAllergies().size());
    }

    private ArrayList<AllergyHistory> getAllergiesHistory() {
        ArrayList<AllergyHistory> allergiesHistory = new ArrayList<AllergyHistory>();
        AllergyHistory specifiedAllergy = new AllergyHistory();
        AllergyHistory notSpecifiedAllergy = new AllergyHistory();

        specifiedAllergy.setSpecified(true);
        allergiesHistory.add(specifiedAllergy);
        allergiesHistory.add(notSpecifiedAllergy);

        return allergiesHistory;
    }
}
