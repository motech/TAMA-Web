package org.motechproject.tama.patient.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class DrugDosageTest {
    @Test
    public void shouldNotSetDefaultEndDate() {
        LocalDate startDate = DateUtil.newDate(2010, 10, 10);

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate);

        assertEquals(null, drugDosage.getEndDate());
    }

    @Test
    public void shouldReturnNonEmptySchedules() {
        DrugDosage dosageWithoutEveningTime = new DrugDosage() {{
            setMorningTime("10:00am");
        }};
        DrugDosage dosageWithoutMorningTime = new DrugDosage() {{
            setEveningTime("10:00pm");
        }};
        DrugDosage dosage = new DrugDosage() {{
            setMorningTime("10:00am");
            setEveningTime("10:00pm");
        }};

        assertEquals(1, dosageWithoutEveningTime.getNonEmptyDosageSchedules().size());
        assertEquals(1, dosageWithoutMorningTime.getNonEmptyDosageSchedules().size());
        assertEquals(2, dosage.getNonEmptyDosageSchedules().size());
    }

    @Test
    public void shouldGetMorningDoseTrackingStartDate_WhenPatientHasNotChangedHisCallPreference() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        final LocalDate today = DateUtil.today();
        DrugDosage drugDosage = new DrugDosage() {{
            this.setStartDate(today);
        }};
        LocalDate startDate = drugDosage.morningDoseTrackingStartDate(patient);
        assertEquals(today, startDate);
    }

    @Test
    public void shouldGetMorningDoseTrackingStartDate_WhenPatientHasChangedHisCallPreference() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withTransitionDate(DateUtil.today()).build();
        final LocalDate tenDaysBack = DateUtil.today().minusDays(10);
        DrugDosage drugDosage = new DrugDosage() {{
            this.setStartDate(tenDaysBack);
        }};
        LocalDate startDate = drugDosage.morningDoseTrackingStartDate(patient);
        assertEquals(DateUtil.today(), startDate);
    }

    @Test
    public void shouldGetEveningDoseTrackingStartDate_WhenPatientHasNotChangedHisCallPreference() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        final LocalDate today = DateUtil.today();
        DrugDosage drugDosage = new DrugDosage() {{
            this.setStartDate(today);
        }};
        LocalDate startDate = drugDosage.eveningDoseTrackingStartDate(patient);
        assertEquals(DateUtil.today(), startDate);
    }

    @Test
    public void shouldGetEveningDoseTrackingStartDate_WhenPatientHasChangedHisCallPreference() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withTransitionDate(DateUtil.today()).build();
        final LocalDate tenDaysAgo = DateUtil.today().minusDays(10);
        DrugDosage drugDosage = new DrugDosage() {{
            this.setStartDate(tenDaysAgo);
        }};
        LocalDate startDate = drugDosage.eveningDoseTrackingStartDate(patient);
        assertEquals(DateUtil.today(), startDate);
    }

    @Test
    public void shouldGetEveningDoseTrackingStartDate_WhenPatientHasChangedHisCallPreference_AndIsOnVariableDosage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withTransitionDate(DateUtil.today()).build();
        DrugDosage drugDosage = new DrugDosage() {{
            this.setStartDate(DateUtil.today().minusDays(10));
            this.setOffsetDays(10);
        }};
        LocalDate startDate = drugDosage.eveningDoseTrackingStartDate(patient);
        assertEquals(DateUtil.today(), startDate);
    }

    @Test
    public void shouldGetEveningDoseTrackingStartDate_WhenCallPreferenceTransitionDateIsBeforeDosageDate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withTransitionDate(DateUtil.today()).build();
        final LocalDate today = DateUtil.today();
        DrugDosage drugDosage = new DrugDosage() {{
            this.setStartDate(today);
            this.setOffsetDays(10);
        }};
        LocalDate startDate = drugDosage.eveningDoseTrackingStartDate(patient);
        assertEquals(today.plusDays(10), startDate);
    }
}
