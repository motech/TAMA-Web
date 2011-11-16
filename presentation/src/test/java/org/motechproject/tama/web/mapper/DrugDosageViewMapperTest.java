package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.repository.AllDosageTypes;
import org.motechproject.tama.repository.AllDrugs;
import org.motechproject.tama.repository.AllMealAdviceTypes;
import org.motechproject.tama.web.model.DrugDosageView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DrugDosageViewMapperTest {

    private AllDrugs allDrugs;
    private AllDosageTypes allDosageTypes;
    private AllMealAdviceTypes allMealAdviceTypes;
    private DrugDosage drugDosage;

    @Before
    public void setUp() {
        allDrugs = mock(AllDrugs.class);
        allDosageTypes = mock(AllDosageTypes.class);
        allMealAdviceTypes = mock(AllMealAdviceTypes.class);
        drugDosage = getDrugDosage();
    }

    @Test
    public void shouldReturnDrugDosageViewForDrugDosage() {
        Drug drug = DrugBuilder.startRecording().withDefaults().build();
        when(allDrugs.get("drugId")).thenReturn(drug);

        DosageType dosageType = new DosageType("Once");
        when(allDosageTypes.get("dosageTypeId")).thenReturn(dosageType);

        MealAdviceType mealAdviceType = new MealAdviceType("After Meal");
        when(allMealAdviceTypes.get("mealAdviceTypeId")).thenReturn(mealAdviceType);

        DrugDosageView drugDosageView = new DrugDosageViewMapper(allDrugs, allDosageTypes, allMealAdviceTypes).map(drugDosage);

        Assert.assertEquals("drugName", drugDosageView.getDrugName());
        Assert.assertEquals("brandName", drugDosageView.getBrandName());
        Assert.assertEquals("Once", drugDosageView.getDosageType());
        Assert.assertEquals("advice", drugDosageView.getAdvice());
        Assert.assertEquals("After Meal", drugDosageView.getMealAdviceType());
    }

    private DrugDosage getDrugDosage() {
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setDrugId("drugId");
        drugDosage.setBrandId("brandId");
        drugDosage.setDosageTypeId("dosageTypeId");
        drugDosage.setAdvice("advice");
        drugDosage.setMealAdviceId("mealAdviceTypeId");
        return drugDosage;
    }
}
