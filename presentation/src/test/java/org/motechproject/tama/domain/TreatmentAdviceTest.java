package org.motechproject.tama.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

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

    @Test
    public void getEndDateShouldGiveTheEndDateOfDrugDosageHavingTheLastEndDate() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        LocalDate today = DateUtil.today();

        DrugDosage doseEndingToday = new DrugDosage();
        doseEndingToday.setEndDate(today);
        DrugDosage doseEndingTomorrow = new DrugDosage();
        doseEndingTomorrow.setEndDate(today.plusDays(1));
        DrugDosage doseEndingDayAfter = new DrugDosage();
        doseEndingDayAfter.setEndDate(today.plusDays(2));

        treatmentAdvice.addDrugDosage(doseEndingToday);
        treatmentAdvice.addDrugDosage(doseEndingTomorrow);
        treatmentAdvice.addDrugDosage(doseEndingDayAfter);

        assertEquals(treatmentAdvice.getEndDate(), doseEndingDayAfter.getEndDateAsDate());
    }

    @Test
    public void getEndDateShouldGiveTheEndDateOfDrugDosageHavingNoEndDate() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        LocalDate today = DateUtil.today();

        DrugDosage doseEndingToday = new DrugDosage();
        doseEndingToday.setEndDate(today);
        DrugDosage doseEndingTomorrow = new DrugDosage();
        doseEndingTomorrow.setEndDate(today.plusDays(1));
        DrugDosage doseWithNoEndCaptured = new DrugDosage();
        doseWithNoEndCaptured.setEndDate(null);

        treatmentAdvice.addDrugDosage(doseEndingToday);
        treatmentAdvice.addDrugDosage(doseEndingTomorrow);
        treatmentAdvice.addDrugDosage(doseWithNoEndCaptured);

        assertEquals(treatmentAdvice.getEndDate(), doseWithNoEndCaptured.getEndDateAsDate());
    }

    @Test
    public void getStartDateShouldGiveTheStartDateOfDrugDosageHavingTheLeastStartDate() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        LocalDate today = DateUtil.today();

        DrugDosage doseStartingToday = new DrugDosage();
        doseStartingToday.setStartDate(today);
        DrugDosage doseStartingTomorrow = new DrugDosage();
        doseStartingTomorrow.setStartDate(today.plusDays(1));
        DrugDosage doseStartingDayAfter = new DrugDosage();
        doseStartingDayAfter.setStartDate(today.plusDays(2));
        DrugDosage doseThatDoesNotStart = new DrugDosage();
        doseThatDoesNotStart.setStartDate(null);

        treatmentAdvice.addDrugDosage(doseStartingToday);
        treatmentAdvice.addDrugDosage(doseStartingTomorrow);
        treatmentAdvice.addDrugDosage(doseStartingDayAfter);
        treatmentAdvice.addDrugDosage(doseThatDoesNotStart);

        assertEquals(treatmentAdvice.getStartDate(), doseStartingToday.getStartDateAsDate());
    }

    @Test
    public void shouldGroupDosagesByTime() {
        final String morningTime = "10:00am";
        final String eveningTime = "10:00pm";

        TreatmentAdvice advice = new TreatmentAdvice() {{
            addDrugDosage(new DrugDosage() {{
            	setMorningTime(morningTime);
            	setEveningTime(eveningTime);
            }});
            addDrugDosage(new DrugDosage() {{
                setMorningTime(morningTime);
            }});
            addDrugDosage(new DrugDosage() {{
                setEveningTime(eveningTime);
            }});
        }};
        final Map<String, List<DrugDosage>> dosageGroups = advice.groupDosagesByTime();
        assertFalse(dosageGroups.isEmpty());
        assertEquals(2, dosageGroups.size());
        assertTrue(dosageGroups.containsKey(morningTime));
        assertTrue(dosageGroups.containsKey(eveningTime));

        assertEquals(2, dosageGroups.get(morningTime).size());
        assertEquals(2, dosageGroups.get(eveningTime).size());
    }

    public void shouldReturnTrueIfTreatmentAdviceHasMultipleDosages() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        LocalDate startDateForDrug1 = DateUtil.newDate(2010, 10, 10);
        LocalDate endDateForDrug1 = DateUtil.newDate(2010, 12, 10);

        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, "09:00am", "08:30pm"));
        treatmentAdvice.setDrugDosages(drugDosages);

        assertTrue(treatmentAdvice.hasMultipleDosages());
    }

    @Test
    public void shouldReturnFalseIfTreatmentAdviceHasSingleDosage() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        LocalDate startDateForDrug1 = DateUtil.newDate(2010, 10, 10);
        LocalDate endDateForDrug1 = DateUtil.newDate(2010, 12, 10);

        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, "09:00am",""));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug1, endDateForDrug1, "09:00am",""));
        
        treatmentAdvice.setDrugDosages(drugDosages);

        assertFalse(treatmentAdvice.hasMultipleDosages());
    }
    
    @Test
    public void shouldReturnFalseIfTreatmentAdviceHasVariableDoseAndEveningDoseIsNotStartedYet() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        LocalDate startDateForDrug1 = DateUtil.newDate(2010, 10, 10);
        LocalDate endDateForDrug1 = DateUtil.newDate(2010, 12, 10);

        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, "09:00am",""));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug1, endDateForDrug1, "09:00am",""));
        DrugDosage drugDosage = drugDosage("Drug2Id", DateUtil.today(), null, "", "09:00pm");
        drugDosage.setOffsetDays(10);
		drugDosages.add(drugDosage);
        
        treatmentAdvice.setDrugDosages(drugDosages);

        assertFalse(treatmentAdvice.hasMultipleDosages());
    }
    
    @Test
    public void shouldReturnTrueIfTreatmentAdviceHasVariableDoseAndEveningDoseIsNotStartedYet() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        LocalDate startDateForDrug1 = DateUtil.newDate(2010, 10, 10);
        LocalDate endDateForDrug1 = DateUtil.newDate(2010, 12, 10);

        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, "09:00am",""));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug1, endDateForDrug1, "09:00am",""));
        DrugDosage drugDosage = drugDosage("Drug2Id", DateUtil.today().minusDays(10), null, "", "09:00pm");
        drugDosage.setOffsetDays(10);
		drugDosages.add(drugDosage);
        
        treatmentAdvice.setDrugDosages(drugDosages);

        assertTrue(treatmentAdvice.hasMultipleDosages());
    }

    private DrugDosage drugDosage(final String drugId, final LocalDate startDate, final LocalDate endDate, final String morningTime, final String eveningTime) {
        return new DrugDosage() {{
            setDrugId(drugId);
            setBrandId("brandId");
            setStartDate(startDate);
            setEndDate(endDate);
            setMorningTime(morningTime);
            setEveningTime(eveningTime);
        }};
    }
}
