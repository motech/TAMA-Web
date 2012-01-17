package org.motechproject.tama.ivr.service;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.AdherenceComplianceReport;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;


public class AdherenceServiceTest {

    @Mock
    Patient patient;
    @Mock
    AdherenceServiceStrategy dailyReminderAdherenceService;
    @Mock
    AdherenceServiceStrategy fourDayRecallService;

    private AdherenceService adherenceService;

    @Before
    public void setup() {
        initMocks(this);
        adherenceService = new AdherenceService();
    }

    @Test
    public void shouldReturnAdherenceReport_WhenPatientIsOnDaily() {
        adherenceService.register(CallPreference.DailyPillReminder, dailyReminderAdherenceService);
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.DailyPillReminder).build();
        when(dailyReminderAdherenceService.wasAnyDoseMissedLastWeek(patient)).thenReturn(false);
        when(dailyReminderAdherenceService.wasAnyDoseTakenLateLastWeek(patient)).thenReturn(true);
        final AdherenceComplianceReport adherenceComplianceReport = adherenceService.lastWeekAdherence(patient);
        assertFalse(adherenceComplianceReport.missed());
        assertTrue(adherenceComplianceReport.late());
        verifyZeroInteractions(fourDayRecallService);

    }

    @Test
    public void shouldReturnAdherenceReport_WhenPatientIsOnWeekly() {
        adherenceService.register(CallPreference.FourDayRecall, fourDayRecallService);
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.FourDayRecall).build();
        when(fourDayRecallService.wasAnyDoseMissedLastWeek(patient)).thenReturn(true);
        when(fourDayRecallService.wasAnyDoseTakenLateLastWeek(patient)).thenReturn(false);
        final AdherenceComplianceReport adherenceComplianceReport = adherenceService.lastWeekAdherence(patient);
        assertTrue(adherenceComplianceReport.missed());
        assertFalse(adherenceComplianceReport.late());
        verifyZeroInteractions(dailyReminderAdherenceService);
    }
}
