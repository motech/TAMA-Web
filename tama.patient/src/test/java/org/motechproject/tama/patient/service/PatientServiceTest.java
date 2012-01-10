package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest extends BaseUnitTest {

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
        registerKnownCallPlans();
    }

    private void registerKnownCallPlans() {
        patientService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        patientService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);
    }

    @Test
    public void shouldCreatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        patientService.create(patient, "clinicId");

        verify(allPatients).addToClinic(patient, "clinicId");
        verify(outbox).enroll(patient);
    }

    @Test
    public void shouldActivatePatient() {
        DateTime now = DateUtil.newDateTime(DateUtil.today(), 10, 0, 0);
        mockCurrentDate(now);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId("Id").build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.activate(patient.getId());

        verify(allPatients).update(patient);
        assertEquals(now, patient.getActivationDate());
    }

    @Test
    public void shouldDeactivatePatient() {
        DateTime now = DateUtil.newDateTime(DateUtil.today(), 10, 0, 0);
        mockCurrentDate(now);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId("Id").build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.deactivate(patient.getId(), Status.Temporary_Deactivation);

        verify(allPatients).update(patient);
        assertEquals(Status.Temporary_Deactivation, patient.getStatus());
        assertEquals(now, patient.getLastDeactivationDate());
    }

    @Test
    public void shouldSuspendPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.suspend(patient.getId());

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(patientArgumentCaptor.capture());
        assertEquals(Status.Suspended, patientArgumentCaptor.getValue().getStatus());
        assertEquals(DateUtil.today(), patientArgumentCaptor.getValue().getLastSuspendedDate().toLocalDate());
    }

    @Test
    public void patientUpdates_ButNotChangeHisCallPlan() {
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
    public void shouldNotModifyDateFieldsOnUpdate() {
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withRegistrationDate(today).
                withActivationDate(now).withLastSuspendedDate(now.plusDays(10)).withLastDeactivationDate(now.plusDays(4)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withPasscode("9999").build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertEquals(dbPatient.getActivationDate(), patient.getActivationDate());
        assertEquals(dbPatient.getLastDeactivationDate(), patient.getLastDeactivationDate());
        assertEquals(dbPatient.getLastSuspendedDate(), patient.getLastSuspendedDate());
        assertEquals(dbPatient.getRegistrationDate(), patient.getRegistrationDate());
    }


    @Test
    public void patientChangesHisCallPlanDailyToWeekly() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(dailyCallPlan).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(weeklyCallPlan).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }

    @Test
    public void patientChangesHisCallPlanWeeklyToDaily() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(dailyCallPlan).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient);
    }

    @Test
    public void patientChangesHisDayOfWeeklyCall() {
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
