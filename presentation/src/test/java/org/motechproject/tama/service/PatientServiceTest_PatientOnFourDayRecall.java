package org.motechproject.tama.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.view.SuspendedAdherenceData;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest_PatientOnFourDayRecall {

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
    private WeeklyAdherenceService weeklyAdherenceService;
    @Mock
    private DosageAdherenceService dosageAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.FourDayRecall)
                .withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(tamaSchedulerService, pillReminderService, allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields, allVitalStatistics, weeklyAdherenceService, dosageAdherenceService);
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
    public void shouldNotUnschedulePillReminderCalls_WhenCallPreferenceIsNotChanged() {
        PatientPreferences patientPreferences = dbPatient.getPatientPreferences();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(patientPreferences.getCallPreference()).withBestCallTime(patientPreferences.getBestCallTime()).withId("patient_id").build();
        patientService.update(patient);
        verify(pillReminderService, never()).unscheduleJobs(patient.getId());
    }


    @Test
    public void shouldScheduleOutboxJobs_WhenPatientChangedFromWeeklyToDailyPillReminder() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService).scheduleJobForOutboxCall(patient);
    }

    @Test
    public void shouldNotScheduleOutboxJobs_WhenCallPreferenceIsWeekly_AndBestCallTimeChanged() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        patientService.update(patient);
        verify(tamaSchedulerService, never()).scheduleJobForOutboxCall(patient);
    }

    @Test
    public void shouldUpdate_WeeklyAdherenceLogsForPatientOnFourDayRecall() {
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        DateTime suspendedDate = DateUtil.now();
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("patientId").withLastSuspendedDate(suspendedDate).withCallPreference(CallPreference.FourDayRecall).build();
        when(allPatients.get("patientId")).thenReturn(patient);
        patientService.reActivate("patientId", suspendedAdherenceData);
        verify(weeklyAdherenceService).recordAdherence(suspendedAdherenceData);
        assertEquals(suspendedDate, suspendedAdherenceData.suspendedFrom());
    }

}
