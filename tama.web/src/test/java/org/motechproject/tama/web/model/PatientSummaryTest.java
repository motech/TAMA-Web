package org.motechproject.tama.web.model;

import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientEventLog;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PatientSummaryTest {

    @Test
    public void shouldHaveBestCallTime() {
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(bestCallTime).build();
        PatientSummary patientSummary = new PatientSummary(patient, null, null, null, null, Collections.<PatientEventLog>emptyList(), 10d, null);

        assertEquals("10:10 AM", patientSummary.getBestCallTime());
    }

    @Test
    public void shouldHaveDayOfPreferenceForWeeklyAdherencePatients() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Friday).build();
        PatientSummary patientSummary = new PatientSummary(patient, null, null, null, null, Collections.<PatientEventLog>emptyList(), 10d, null);

        assertEquals("Friday", patientSummary.getDayOfWeeklyCall());
    }

    @Test
    public void shouldHaveEmptyDayOfPreferenceForDailyPillReminderPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        PatientSummary patientSummary = new PatientSummary(patient, null, null, null, null, Collections.<PatientEventLog>emptyList(), 10d, null);

        assertEquals("", patientSummary.getDayOfWeeklyCall());
    }

    @Test
    public void shouldTakeOnlyTheIntegerPartOfAdherencePercentage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        PatientSummary patientSummary = new PatientSummary(patient, null, null, null, null, Collections.<PatientEventLog>emptyList(), 10.12d, null);

        assertEquals("10%", patientSummary.getRunningAdherencePercentage());
    }
}
