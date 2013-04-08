package org.motechproject.tama.messages.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendServiceTest {

    @Mock
    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    private AdherenceTrendService adherenceTrendService;

    @Before
    public void setup() {
        initMocks(this);
        adherenceTrendService = new AdherenceTrendService(dailyPillReminderAdherenceService, fourDayRecallAdherenceService);
    }

    @Test
    public void shouldReturnAdherencePercentageForPatientWithDailyPillReminderPreference() throws NoAdherenceRecordedException {
        double adherencePercentage = 60;
        DateTime now = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withId("patientDocId").withCallPreference(CallPreference.DailyPillReminder).build();

        when(dailyPillReminderAdherenceService.getAdherencePercentage(patient.getId(), now)).thenReturn(adherencePercentage);

        assertEquals(adherencePercentage, adherenceTrendService.getAdherencePercentage(patient, now));
    }

    @Test
    public void shouldReturnAdherencePercentageForPatientWithFourDayPillReminderPreference() throws NoAdherenceRecordedException {
        double adherencePercentage = 60;
        DateTime now = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withId("patientDocId").withCallPreference(CallPreference.FourDayRecall).build();

        when(fourDayRecallAdherenceService.getRunningAdherencePercentage(patient)).thenReturn(adherencePercentage);

        assertEquals(adherencePercentage, adherenceTrendService.getAdherencePercentage(patient, now));
    }

    @Test
    public void shouldReturnZeroAsAdherenceWhenAdherenceWasNotRecorded() throws NoAdherenceRecordedException {
        DateTime now = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withId("patientDocId").withCallPreference(CallPreference.DailyPillReminder).build();

        when(dailyPillReminderAdherenceService.getAdherencePercentage(patient.getId(), now)).thenThrow(new NoAdherenceRecordedException(""));
        assertEquals(0d, adherenceTrendService.getAdherencePercentage(patient, now));
    }
}
