package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.refdata.builder.DrugBuilder;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.objectcache.AllDosageTypesCache;
import org.motechproject.tama.refdata.objectcache.AllDrugsCache;
import org.motechproject.tama.refdata.objectcache.AllMealAdviceTypesCache;
import org.motechproject.tama.web.model.DrugDosageView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DrugDosageViewMapperTest {

    private AllDrugsCache allDrugs;
    private AllDosageTypesCache allDosageTypes;
    private AllMealAdviceTypesCache allMealAdviceTypes;
    private DrugDosage drugDosage;

    @Before
    public void setUp() {
        allDrugs = mock(AllDrugsCache.class);
        allDosageTypes = mock(AllDosageTypesCache.class);
        allMealAdviceTypes = mock(AllMealAdviceTypesCache.class);
        drugDosage = getDrugDosage();
    }

    @Test
    public void shouldReturnDrugDosageViewForDrugDosage() {
        Drug drug = DrugBuilder.startRecording().withDefaults().build();
        when(allDrugs.getBy("drugId")).thenReturn(drug);

        DosageType dosageType = new DosageType("Once");
        when(allDosageTypes.getBy("dosageTypeId")).thenReturn(dosageType);

        MealAdviceType mealAdviceType = new MealAdviceType("After Meal");
        when(allMealAdviceTypes.getBy("mealAdviceTypeId")).thenReturn(mealAdviceType);

        DrugDosageView drugDosageView = new DrugDosageViewMapper(allDrugs, allDosageTypes, allMealAdviceTypes).map(drugDosage);

        Assert.assertEquals("drugName", drugDosageView.getDrugName());
        Assert.assertEquals("Once", drugDosageView.getDosageType());
        Assert.assertEquals("advice", drugDosageView.getAdvice());
        Assert.assertEquals("After Meal", drugDosageView.getMealAdviceType());
    }

    private DrugDosage getDrugDosage() {
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setDrugId("drugId");
        drugDosage.setDosageTypeId("dosageTypeId");
        drugDosage.setAdvice("advice");
        drugDosage.setMealAdviceId("mealAdviceTypeId");
        return drugDosage;
    }
}
