package org.motechproject.tama.patient.strategy;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangedPatientPreferenceContextTest extends BaseUnitTest {

    public static final String PATIENT_DOC_ID = "patientDocId";

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

    @Test
    public void shouldReturnPatientEventLogs_WhenCallPlanHasChanged() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withCallPreference(CallPreference.FourDayRecall).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertPatientEventLog(changedPatientPreferenceContext.getEventLogs().get(0), PatientEvent.Call_Plan_Changed, CallPreference.FourDayRecall.name(), now);
    }

    @Test
    public void shouldReturnPatientEventLogs_WhenDayOfCallHasChanged() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withDayOfWeeklyCall(DayOfWeek.Sunday).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertPatientEventLog(changedPatientPreferenceContext.getEventLogs().get(0), PatientEvent.Day_Of_Weekly_Call_Changed, DayOfWeek.Sunday.name(), now);
    }

    @Test
    public void shouldReturnPatientEventLogs_WhenBestCallTimeHasChanged() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withBestCallTime(new TimeOfDay(12, 10, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertPatientEventLog(changedPatientPreferenceContext.getEventLogs().get(0), PatientEvent.Best_Call_Time_Changed, patient.getPatientPreferences().getBestCallTime().toString(), now);
    }

    @Test
    public void shouldReturnPatientEventLogs_WhenBestCallTimeIsUnset() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withBestCallTime(new TimeOfDay(null, null, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        assertPatientEventLog(changedPatientPreferenceContext.getEventLogs().get(0), PatientEvent.Best_Call_Time_Changed, "Value was unset", now);
    }

    @Test
    public void shouldReturnPatientEventLogs_WhenPatientIsCreatedOnDailyPillReminder() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).
                withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay()).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(null, patient);
        final List<PatientEventLog> eventLogs = changedPatientPreferenceContext.getEventLogs();
        assertEquals(1, eventLogs.size());
        assertPatientEventLog(eventLogs.get(0), PatientEvent.Call_Plan_Changed, patient.getPatientPreferences().getCallPreference().name(), now);
    }

    @Test
    public void shouldReturnPatientEventLogs_WhenPatientIsCreatedOnWeekly() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).
                withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Saturday).withBestCallTime(new TimeOfDay(12, 10, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(null, patient);
        final List<PatientEventLog> eventLogs = changedPatientPreferenceContext.getEventLogs();
        assertEquals(3, eventLogs.size());
        assertPatientEventLog(eventLogs.get(0), PatientEvent.Call_Plan_Changed, patient.getPatientPreferences().getCallPreference().name(), now);
        assertPatientEventLog(eventLogs.get(1), PatientEvent.Day_Of_Weekly_Call_Changed, patient.getPatientPreferences().getDayOfWeeklyCall().name(), now);
        assertPatientEventLog(eventLogs.get(2), PatientEvent.Best_Call_Time_Changed, patient.getPatientPreferences().getBestCallTime().toString(), now);
    }

    private void assertPatientEventLog(PatientEventLog patientEventLog, PatientEvent patientEvent, String newValue, DateTime now) {
        assertEquals(patientEvent, patientEventLog.getEvent());
        assertEquals(now, patientEventLog.getDate());
        assertEquals(PATIENT_DOC_ID, patientEventLog.getPatientId());
        assertEquals(newValue, patientEventLog.getNewValue());
    }

}
