package org.motechproject.tama.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreatmentAdviceTest {

    private TreatmentAdvice treatmentAdvice;

    @Before
    public void setUp() {
        treatmentAdvice = TreatmentAdvice.newDefault();
    }

    @Test
    public void endTheRegimenShouldSetTodayAsTheEndDateForAllItsDrugs() {
        DrugDosage firstDrug = treatmentAdvice.getDrugDosages().get(0);
        DrugDosage secondDrug = treatmentAdvice.getDrugDosages().get(1);

        assertNull(firstDrug.getEndDate());
        assertNull(secondDrug.getEndDate());

        treatmentAdvice.endTheRegimen();

        assertEquals(DateUtil.today(), firstDrug.getEndDate());
        assertEquals(DateUtil.today(), secondDrug.getEndDate());
    }
}
