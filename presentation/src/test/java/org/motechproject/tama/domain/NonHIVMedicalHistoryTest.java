package org.motechproject.tama.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    @Test
    public void shouldGetAilments_GivenASystemCategory() {
        NonHIVMedicalHistory nonHIVMedicalHistory = new NonHIVMedicalHistory();
        SystemCategoryDefinition systemCategoryDefiniton = SystemCategoryDefinition.Other;
        SystemCategory otherSystemCategory = new SystemCategory(systemCategoryDefiniton.getCategoryName(), systemCategoryDefiniton.getAilments());
        nonHIVMedicalHistory.setSystemCategories(Arrays.asList(otherSystemCategory));

        Ailments otherSystemCategoryAilments = nonHIVMedicalHistory.getAilments(SystemCategoryDefinition.Other);
        assertEquals(5, otherSystemCategoryAilments.getAilments().size());

        Ailments noAilments = nonHIVMedicalHistory.getAilments(SystemCategoryDefinition.Dermatological);
        assertNull(noAilments);
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
