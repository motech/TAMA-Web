package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;

import static org.junit.Assert.assertEquals;
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

    @Before
    public void setUp() {
        initMocks(this);
        dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(11, 20, TimeMeridiem.PM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(tamaSchedulerService, pillReminderService, allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields);
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
    }

    @Test
    public void shouldUnschedulePillReminderCalls_WhenCallPreferenceIsChangedToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(null, null, null)).build();
        patientService.update(patient);
        verify(pillReminderService).unscheduleJobs(patient.getId());
    }

    @Test
    public void shouldNotUnschedulePillReminderCalls_WhenCallPreferenceIsNotChanged() {
        PatientPreferences patientPreferences = dbPatient.getPatientPreferences();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(patientPreferences.getCallPreference()).withBestCallTime(patientPreferences.getBestCallTime()).withId("patient_id").build();
        patientService.update(patient);
        verify(pillReminderService, never()).unscheduleJobs(patient.getId());
    }

    @Test
    public void shouldUnscheduleJobsForAdherenceTrendFeedbackOutboxMessage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(null, null, null)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.findByPatientId(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).unscheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    @Test
    public void shouldScheduleFourDayRecallJobs_WhenCallPreferenceIsChangedToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(null, null, null)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.findByPatientId(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    @Test
    public void shouldUnscheduleOutboxCall_WhenCallPreferenceIsChangedToFourDayRecall() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(null, null, null)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.findByPatientId(patient.getId())).thenReturn(treatmentAdvice);
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
}
