package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;
import org.motechproject.tamacallflow.util.FixedDateTimeSource;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.builder.TreatmentAdviceBuilder;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.repository.*;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest_PatientOnDailyPillReminder {

    private PatientService patientService;
    
    private Patient dbPatient;

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private TamaSchedulerService tamaSchedulerService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllRegimens allRegimens;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(11, 20, TimeMeridiem.PM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(tamaSchedulerService, pillReminderService, allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields, allVitalStatistics, fourDayRecallAdherenceService, dailyReminderAdherenceService);
    }

    @Test
    public void shouldUpdatePatient() {
        CallPreference callPreference = dbPatient.getPatientPreferences().getCallPreference();
        TimeOfDay bestCallTime = dbPatient.getPatientPreferences().getBestCallTime();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber("7777777777").withId("patient_id").withCallPreference(callPreference).withBestCallTime(bestCallTime).build();
        patientService.update(patient);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(captor.capture());
        assertEquals(captor.getValue().getMobilePhoneNumber(), "7777777777");
        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
    }

    @Test
    public void shouldUnschedulePillReminderCalls_WhenCallPreferenceIsChangedToFourDayRecall_AndPatientHasATreatmentAdvice() {
        final DateTime now = DateUtil.now();
        DateTimeSourceUtil.SourceInstance = new FixedDateTimeSource(now);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(null, null, null)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withPatientId("patient_id").build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);

        verify(pillReminderService).remove(patient.getId());
        assertEquals(now.getMillis(), patient.getPatientPreferences().getCallPreferenceTransitionDate().getMillis());
    }

    @Test
    public void shouldNotUnschedulePillReminderCalls_WhenCallPreferenceIsChangedToFourDayRecall_AndPatientDoesNotHaveATreatmentAdvice() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(null, null, null)).build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(null);

        patientService.update(patient);
        verify(pillReminderService, never()).remove(patient.getId());
    }

    @Test
    public void shouldNotUnschedulePillReminderCalls_WhenCallPreferenceIsNotChanged() {
        PatientPreferences patientPreferences = dbPatient.getPatientPreferences();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(patientPreferences.getCallPreference()).withBestCallTime(patientPreferences.getBestCallTime()).withId("patient_id").build();
        patientService.update(patient);
        verify(pillReminderService, never()).remove(patient.getId());
    }

    @Test
    public void shouldUnscheduleJobsForAdherenceTrendFeedbackOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(null, null, null)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).unscheduleJobForAdherenceTrendFeedbackForDailyPillReminder(treatmentAdvice);
        verify(tamaSchedulerService).unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient);
    }

    @Test
    public void shouldScheduleFourDayRecallJobs_WhenCallPreferenceIsChangedToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(null, null, null)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    @Test
    public void shouldUnscheduleOutboxCall_WhenCallPreferenceIsChangedToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(null, null, null)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).unscheduleJobForOutboxCall(patient);
        verify(tamaSchedulerService).unscheduleRepeatingJobForOutboxCall(patient.getId());
    }

    @Test
    public void shouldUpdateOutboxJobs_WhenBestCallTimeHasChanged() {
        Patient updatedPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(10, 00, TimeMeridiem.AM)).build();
        updatedPatient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
        Patient patientFromDb = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(10, 00, TimeMeridiem.PM)).build();
        when(allPatients.get(updatedPatient.getId())).thenReturn(patientFromDb);
        patientService.update(updatedPatient);
        verify(tamaSchedulerService).unscheduleJobForOutboxCall(patientFromDb);
        verify(tamaSchedulerService).unscheduleRepeatingJobForOutboxCall(patientFromDb.getId());
        verify(tamaSchedulerService).scheduleJobForOutboxCall(updatedPatient);
        verify(tamaSchedulerService, never()).scheduleRepeatingJobForOutBoxCall(updatedPatient);
    }

    @Test
    public void shouldUpdate_DosageAdherenceLogsForPatientOnDailyPillReminder(){
        DateTime suspendedDate = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("patientId").withLastSuspendedDate(suspendedDate).withCallPreference(CallPreference.DailyPillReminder).build();
        when(allPatients.get("patientId")).thenReturn(patient);
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        patientService.reActivate("patientId", suspendedAdherenceData);
        verify(dailyReminderAdherenceService).recordAdherence(suspendedAdherenceData);
        assertEquals(suspendedDate, suspendedAdherenceData.suspendedFrom());
    }

}
