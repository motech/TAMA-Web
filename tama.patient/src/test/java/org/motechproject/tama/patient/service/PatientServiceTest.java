package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.patient.strategy.CallPlan;
import org.motechproject.tama.patient.strategy.Outbox;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
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
    private AllRegimens allRegimens;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private AllPatientEventLogs allPatientEventLogs;
    @Mock
    private Outbox outbox;
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    private PatientService patientService;
    final private String USER_NAME = "userName";

    @Before
    public void setUp() {
        initMocks(this);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(11, 20, TimeMeridiem.PM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(allPatients, allTreatmentAdvices, allRegimens, allPatientEventLogs);
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

        patientService.create(patient, "clinicId", USER_NAME);

        verify(allPatients).addToClinic(patient, "clinicId", USER_NAME);
        verify(outbox).enroll(patient);
    }

    @Test
    public void shouldActivatePatient() {
        DateTime now = DateUtil.newDateTime(DateUtil.today(), 10, 0, 0);
        mockCurrentDate(now);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId("Id").build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.activate(patient.getId(), USER_NAME);

        verify(allPatients).update(patient, USER_NAME);
        assertEquals(now, patient.getActivationDate());

        ArgumentCaptor<PatientEventLog> eventLogArgumentCaptor = ArgumentCaptor.forClass(PatientEventLog.class);
        verify(allPatientEventLogs).add(eventLogArgumentCaptor.capture());
        assertEquals(PatientEvent.Activation, eventLogArgumentCaptor.getValue().getEvent());
        assertEquals(patient.getId(), eventLogArgumentCaptor.getValue().getPatientId());
        assertEquals(DateUtil.now(), eventLogArgumentCaptor.getValue().getDate());
    }

    @Test
    public void shouldDeactivatePatient() {
        DateTime now = DateUtil.newDateTime(DateUtil.today(), 10, 0, 0);
        mockCurrentDate(now);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId("Id").build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.deactivate(patient.getId(), Status.Temporary_Deactivation, USER_NAME);

        verify(allPatients).update(patient, USER_NAME);
        assertEquals(Status.Temporary_Deactivation, patient.getStatus());
        assertEquals(now, patient.getLastDeactivationDate());

        ArgumentCaptor<PatientEventLog> eventLogArgumentCaptor = ArgumentCaptor.forClass(PatientEventLog.class);
        verify(allPatientEventLogs).add(eventLogArgumentCaptor.capture());
        assertEquals(PatientEvent.Temporary_Deactivation, eventLogArgumentCaptor.getValue().getEvent());
        assertEquals(patient.getId(), eventLogArgumentCaptor.getValue().getPatientId());
        assertEquals(now, eventLogArgumentCaptor.getValue().getDate());

    }

    @Test
    public void shouldSuspendPatient() {
        DateTime now = DateUtil.newDateTime(DateUtil.today(), 10, 0, 0);
        mockCurrentDate(now);

        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        patientService.suspend(patient.getId(), USER_NAME);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(patientArgumentCaptor.capture(), eq(USER_NAME));
        assertEquals(Status.Suspended, patientArgumentCaptor.getValue().getStatus());
        assertEquals(DateUtil.today(), patientArgumentCaptor.getValue().getLastSuspendedDate().toLocalDate());

        ArgumentCaptor<PatientEventLog> eventLogArgumentCaptor = ArgumentCaptor.forClass(PatientEventLog.class);
        verify(allPatientEventLogs).add(eventLogArgumentCaptor.capture());
        assertEquals(PatientEvent.Suspension, eventLogArgumentCaptor.getValue().getEvent());
        assertEquals(patient.getId(), eventLogArgumentCaptor.getValue().getPatientId());
        assertEquals(now, eventLogArgumentCaptor.getValue().getDate());
    }

    @Test
    public void patientUpdates_ButNotChangeHisCallPlan() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withPasscode("9999").build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient, USER_NAME);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox, never()).reEnroll(dbPatient, patient);
        verify(dailyCallPlan, never()).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(weeklyCallPlan, never()).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient, USER_NAME);
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

        patientService.update(patient, USER_NAME);

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

        patientService.update(patient, USER_NAME);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(dailyCallPlan).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(weeklyCallPlan).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient, USER_NAME);
    }

    @Test
    public void patientChangesHisCallPlanWeeklyToDaily() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient, USER_NAME);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).disEnroll(dbPatient, currentTreatmentAdvice);
        verify(dailyCallPlan).enroll(patient, currentTreatmentAdvice);
        verify(allPatients).update(patient, USER_NAME);
    }

    @Test
    public void patientChangesHisDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient, USER_NAME);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox, never()).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(dbPatient, currentTreatmentAdvice);
        verify(allPatients).update(patient, USER_NAME);
    }

    @Test
    public void dailyReminderPatientChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(5, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient, USER_NAME);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(allPatients).update(patient, USER_NAME);
    }

    @Test
    public void weeklyReminderPatientChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(5, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);

        patientService.update(patient, USER_NAME);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(dbPatient, currentTreatmentAdvice);
        verify(allPatients).update(patient, USER_NAME);
    }

    @Test
    public void shouldReturnPatientReport(){
        String patientDocId = "patientDocId";
        LocalDate currentRegimenStartDate = DateUtil.today();
        LocalDate artStartDate = currentRegimenStartDate.minusDays(10);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientDocId).build();
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().withId("regimenId").build();
        TreatmentAdvice earliestTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(artStartDate).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(currentRegimenStartDate).withRegimenId("regimenId").build();

        when(allPatients.get(patientDocId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientDocId)).thenReturn(currentTreatmentAdvice);
        when(allTreatmentAdvices.earliestTreatmentAdvice(patientDocId)).thenReturn(earliestTreatmentAdvice);
        when(allRegimens.get("regimenId")).thenReturn(regimen);

        PatientReport patientReport = patientService.getPatientReport(patientDocId);

        assertEquals(patient, patientReport.getPatient());
        assertEquals(artStartDate.toDate(), patientReport.getARTStartedOn());
        assertEquals(currentRegimenStartDate.toDate(), patientReport.getCurrentRegimenStartDate());
    }

}
