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

    @Test
    public void shouldAddASystemCategory(){
        NonHIVMedicalHistory nonHIVMedicalHistory = new NonHIVMedicalHistory();
        SystemCategory respiratory = new SystemCategory();
        nonHIVMedicalHistory.addSystemCategory(respiratory);
        Assert.assertFalse(nonHIVMedicalHistory.getSystemCategories().isEmpty());
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
