package org.motechproject.tama.patient.factory_girl;

import org.junit.Test;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.testing.utils.BaseUnitTest;

import static akula.factory.Factories.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.*;
import static org.motechproject.util.DateUtil.today;

public class NewTreatmentAdviceTest extends BaseUnitTest {

    @Test
    public void endTheRegimenShouldSetTodayAsTheEndDateForAllItsDrugs() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().setEndDate(null).build(),
                newMorningDose().setEndDate(null).build()))
            .build();
        DrugDosage firstDrug = treatmentAdvice.getDrugDosages().get(0);
        DrugDosage secondDrug = treatmentAdvice.getDrugDosages().get(1);

        assertNull(firstDrug.getEndDate());
        assertNull(secondDrug.getEndDate());

        treatmentAdvice.endTheRegimen("end it!");

        assertEquals("end it!", treatmentAdvice.getReasonForDiscontinuing());
        assertEquals(today(), firstDrug.getEndDate());
        assertEquals(today(), secondDrug.getEndDate());
    }

    @Test
    public void getEndDateShouldGiveTheEndDateOfDrugDosageHavingTheLastEndDate() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().setEndDate(today()).build(),
                newMorningDose().setEndDate(today().plusDays(1)).build(),
                newMorningDose().setEndDate(today().plusDays(2)).build()))
            .build();
        assertEquals(today().plusDays(2).toDate(), treatmentAdvice.getEndDate());
    }

    @Test
    public void getEndDateShouldGiveTheEndDateOfDrugDosageHavingNoEndDate() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().setEndDate(today()).build(),
                newMorningDose().setEndDate(today().plusDays(1)).build(),
                newMorningDose().setEndDate(null).build()))
            .build();
        assertEquals(null, treatmentAdvice.getEndDate());
    }

    @Test
    public void getStartDateShouldGiveTheStartDateOfDrugDosageHavingTheLeastStartDate() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().setStartDate(today()).build(),
                newMorningDose().setStartDate(today().plusDays(1)).build(),
                newMorningDose().setStartDate(today().plusDays(2)).build(),
                newMorningDose().setStartDate(null).build()))
            .build();
        assertEquals(today().toDate(), treatmentAdvice.getStartDate());
    }

    @Test
    public void shouldReturnTrueIfTreatmentAdviceHasMultipleDosages() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newTwiceDailyDose().build()))
            .build();
        assertTrue(treatmentAdvice.hasMultipleDosages());
    }

    @Test
    public void shouldReturnFalseIfTreatmentAdviceHasSingleDosage() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().build(),
                newMorningDose().build()))
            .build();
        assertFalse(treatmentAdvice.hasMultipleDosages());
    }

    @Test
    public void shouldReturnFalseIfTreatmentAdviceHasVariableDoseAndEveningDoseIsNotStartedYet() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().build(),
                newMorningDose().build(),
                newEveningDose().setStartDate(today()).setEndDate(null).setOffsetDays(10).build()))
            .build();
        assertFalse(treatmentAdvice.hasMultipleDosages());
    }

    @Test
    public void shouldReturnTrueIfTreatmentAdviceHasVariableDoseAndEveningDoseIsNotStartedYet() {
        TreatmentAdvice treatmentAdvice = newTreatmentAdvice()
            .setDrugDosages(asList(
                newMorningDose().build(),
                newMorningDose().build(),
                newEveningDose().setStartDate(today().minusDays(10)).setEndDate(null).setOffsetDays(10).build()))
            .build();
        assertTrue(treatmentAdvice.hasMultipleDosages());
    }
}
