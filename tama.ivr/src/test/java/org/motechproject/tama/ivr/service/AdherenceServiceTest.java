package org.motechproject.tama.ivr.service;


import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;


public class AdherenceServiceTest {

    @Mock
    Patient patient;

    @Mock
    TreatmentAdvice treatmentAdvice;

    @Mock
    AdherenceServiceStratergy dailyReminderAdherenceService;

    @Mock
    AdherenceServiceStratergy fourDayRecallService;

    private AdherenceService adherenceService;

    @Before
    public void setup() {
        initMocks(this);
        adherenceService = new AdherenceService();
    }

    @Test
    public void shouldCheckForAnyMissedDosageInThePreviousWeekForDailyReminderPatient() {
        adherenceService.register(CallPreference.DailyPillReminder, dailyReminderAdherenceService);
        patient = PatientBuilder.startRecording().withId("externalId").withPatientId("patientId").withCallPreference(CallPreference.DailyPillReminder).build();
        when(dailyReminderAdherenceService.isDosageMissedLastWeek(patient)).thenReturn(true);
        assertTrue(adherenceService.isDosageMissedLastWeek(patient));
    }

    @Test
    public void shouldCheckForAnyMissedDosageInThePreviousWeekForFourDayRecallPatient() {
        adherenceService.register(CallPreference.FourDayRecall, fourDayRecallService);
        patient = PatientBuilder.startRecording().withId("externalId").withPatientId("patientId").withCallPreference(CallPreference.FourDayRecall).build();
        when(fourDayRecallService.isDosageMissedLastWeek(patient)).thenReturn(true);
        assertTrue(adherenceService.isDosageMissedLastWeek(patient));
    }

    @Test
    public void anyDoseTakenLateSinceShouldBeTrueWhenAnyDoseWasTakenLateSinceGivenTime_PatientOnDailyPillReminder() {
        adherenceService.register(CallPreference.DailyPillReminder, dailyReminderAdherenceService);
        patient = PatientBuilder.startRecording().withId("externalId").withPatientId("patientId").withCallPreference(CallPreference.DailyPillReminder).build();
        LocalDate since = DateUtil.newDate(2011, 10, 1);
        when(dailyReminderAdherenceService.anyDoseTakenLateSince(patient, since)).thenReturn(true);
        assertTrue(adherenceService.anyDoseTakenLateSince(patient, since));
    }

    @Test
    public void doseTakenLateShouldReturnFalseWhenPatientIsOnFourDayRecall() {
        adherenceService.register(CallPreference.FourDayRecall, fourDayRecallService);
        patient = PatientBuilder.startRecording().withId("externalId").withPatientId("patientId").withCallPreference(CallPreference.FourDayRecall).build();
        LocalDate since = DateUtil.newDate(2011, 10, 1);
        assertFalse(adherenceService.anyDoseTakenLateSince(patient, since));
        verifyZeroInteractions(dailyReminderAdherenceService);
    }
}
