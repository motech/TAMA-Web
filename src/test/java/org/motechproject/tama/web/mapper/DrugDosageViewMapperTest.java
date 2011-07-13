package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.repository.DosageTypes;
import org.motechproject.tama.repository.Drugs;
import org.motechproject.tama.repository.MealAdviceTypes;
import org.motechproject.tama.web.model.DrugDosageView;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DrugDosageViewMapperTest {

    private Drugs drugs;
    private DosageTypes dosageTypes;
    private MealAdviceTypes mealAdviceTypes;
    private DrugDosage drugDosage;

    @Before
    public void setUp() {
        drugs = mock(Drugs.class);
        dosageTypes = mock(DosageTypes.class);
        mealAdviceTypes = mock(MealAdviceTypes.class);
        drugDosage = getDrugDosage();
    }

    @Test
    public void shouldReturnDrugDosageViewForDrugDosage() {
        Drug drug = DrugBuilder.startRecording().withDefaults().build();
        when(drugs.get("drugId")).thenReturn(drug);

        DosageType dosageType = new DosageType("Once");
        when(dosageTypes.get("dosageTypeId")).thenReturn(dosageType);

        MealAdviceType mealAdviceType = new MealAdviceType("After Meal");
        when(mealAdviceTypes.get("mealAdviceTypeId")).thenReturn(mealAdviceType);

        DrugDosageView drugDosageView = new DrugDosageViewMapper(drugs, dosageTypes, mealAdviceTypes).map(drugDosage);

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
        drugDosage.setDosageSchedules(new ArrayList<String>());
        drugDosage.setAdvice("advice");
        drugDosage.setMealAdviceId("mealAdviceTypeId");
        return drugDosage;
    }
}
