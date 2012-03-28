package org.motechproject.tama.patient.strategy;

import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangedPatientPreferenceContextTest {

    @Test
    public void callPlanHasChangedShouldReturnTrue_WhenPatientChangesFromDailyToWeekly() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertTrue(changedPatientPreferenceContext.callPlanHasChanged());
    }

    @Test
    public void callPlanHasChangedShouldReturnFalse_WhenThereAreNoChangesInTheCallPlan() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertFalse(changedPatientPreferenceContext.callPlanHasChanged());
    }

    @Test
    public void bestCallTimeHasChangedShouldReturnTrue_WhenPatientChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertTrue(changedPatientPreferenceContext.bestCallTimeHasChanged());
    }

    @Test
    public void bestCallTimeHasChangedShouldReturnFalse_WhenThereAreNoChangesInBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertFalse(changedPatientPreferenceContext.bestCallTimeHasChanged());
    }

    @Test
    public void dayOfCallHasChangedShouldReturnTrue_WhenPatientChangesHisDayOfCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertTrue(changedPatientPreferenceContext.dayOfCallHasChanged());
    }

    @Test
    public void dayOfCallHasChangedShouldReturnFalse_WhenThereAreNoChangesInDayOfCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertFalse(changedPatientPreferenceContext.dayOfCallHasChanged());
    }

    @Test
    public void patientPreferenceHasChangedShouldReturnTrue_WhenThereAreChangesInAnyOfThePatientPreferences() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertTrue(changedPatientPreferenceContext.patientPreferenceHasChanged());
    }

    @Test
    public void patientPreferenceHasChangedShouldReturnFalse_WhenThereAreNoChangesInAnyOfThePatientPreferences() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertFalse(changedPatientPreferenceContext.patientPreferenceHasChanged());
    }
}
