package org.motechproject.tama.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.motechproject.tama.patient.strategy.Outbox;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private Outbox outbox;
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    private PatientService patientService;
    private Patient dbPatient;

    @Before
    public void setUp() {
        initMocks(this);
        dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(11, 20, TimeMeridiem.PM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(allPatients, allTreatmentAdvices, allUniquePatientFields);
        patientService.registerOutbox(outbox);
    }

    @Test
    public void shouldCreatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        patientService.create(patient, "clinicId");

        verify(allPatients).addToClinic(patient, "clinicId");
        verify(outbox).enroll(patient);
    }

    @Test
    public void shouldSuspendPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.suspend(patient.getId());

        verify(allPatients).update(patient);
        assertThat(patient.getStatus(), is(Status.Suspended));
    }
    
    @Test
    public void patientUpdates_ButNotChangeHisCallPlan() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);

        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withPasscode("9999").build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox, never()).reEnroll(dbPatient, patient);
        verify(dailyCallPlan, never()).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(weeklyCallPlan, never()).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }

    @Test
    public void patientChangesHisCallPlanDailyToWeekly() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);

        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox, never()).reEnroll(dbPatient, patient);
        verify(dailyCallPlan).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(weeklyCallPlan).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }

    @Test
    public void patientChangesHisCallPlanWeeklyToDaily() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);

        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox, never()).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(dailyCallPlan).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }

    @Test
    public void patientChangesHisDayOfWeeklyCall() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);

        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox, never()).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(dbPatient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }

    @Test
    public void dailyReminderPatientChangesHisBestCallTime() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);

        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(05, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(allPatients).update(patient);
    }

    @Test
    public void weeklyReminderPatientChangesHisBestCallTime() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);

        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(05, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(dbPatient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }
}
