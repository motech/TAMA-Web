package org.motechproject.tama.reporting.mapper;

import org.junit.Test;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.junit.Assert.assertEquals;

public class IVRDetailsTest {

    @Test
    public void shouldMapIVRLanguage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals("en", request.getIvrLanguage());
    }

    @Test
    public void shouldMapIVRPassCode() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals("1234", request.getIvrPassCode());
    }

    @Test
    public void shouldMapBestCallTimeWhenBestCallTimeIsNull() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(null).build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals("", request.getBestCallTime());
    }

    @Test
    public void shouldMapBestCallTime() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.PM)).build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals("10:10 PM", request.getBestCallTime());
    }

    @Test
    public void shouldMapCallPreferenceWhenCallPreferenceIsNull() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(null).build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals("", request.getCallPreference());
    }

    @Test
    public void shouldMapCallPreferenceWhenCallPreference() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals("Daily", request.getCallPreference());
    }

    @Test
    public void shouldMapReceiveOTCAdvice() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withOTCPreference(true).build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals(true, request.getReceiveOTCAdvice());
    }

    @Test
    public void shouldMapReceiveAppointmentReminder() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();
        new IVRDetails(patient).copyTo(request);
        assertEquals(true, request.getReceiveAppointmentReminder());
    }
}
