package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.reporting.MedicalHistoryRequestMapper;
import org.motechproject.tama.patient.reporting.PatientRequestMapper;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.motechproject.tama.patient.strategy.CallPlanChangedStrategy;
import org.motechproject.tama.patient.strategy.ChangedPatientPreferenceContext;
import org.motechproject.tama.patient.strategy.PatientPreferenceChangedStrategyFactory;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.motechproject.tama.reports.contract.PatientRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest extends BaseUnitTest {

    public static final String PATIENT_DOC_ID = "patientDocId";
    final private String USER_NAME = "userName";

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllRegimensCache allRegimens;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private AllPatientEventLogs allPatientEventLogs;
    @Mock
    private Outbox outbox;
    @Mock
    private PatientRequestMapper requestMapper;
    @Mock
    private PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory;
    @Mock
    CallPlanChangedStrategy callPlanChangedStrategy;
    @Mock
    private OutboxRegistry outboxRegistry;
    @Mock
    private PatientReportingService patientReportingService;
    @Mock
    private MedicalHistoryRequestMapper medicalHistoryRequestMapper;

    private PatientService patientService;

    @Before
    public void setUp() {
        initMocks(this);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withRevision("revision").withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(11, 20, TimeMeridiem.PM)).build();
        when(allPatients.get(dbPatient.getId())).thenReturn(dbPatient);
        patientService = new PatientService(patientReportingService, requestMapper, allPatients, allTreatmentAdvices, allRegimens, allPatientEventLogs, preferenceChangedStrategyFactory, outboxRegistry, medicalHistoryRequestMapper);
        when(outboxRegistry.getOutbox()).thenReturn(outbox);
    }

    @Test
    public void shouldReportPatientCreation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();

        when(requestMapper.map(patient)).thenReturn(request);
        patientService.create(patient, "clinicId", "user");
        verify(patientReportingService).save(eq(request), any(MedicalHistoryRequest.class));
    }

    @Test
    public void shouldReportPatientUpdate() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();

        when(requestMapper.map(patient)).thenReturn(request);
        when(allPatients.get(anyString())).thenReturn(patient);
        patientService.update(patient, "user");
        verify(patientReportingService).update(eq(request), any(MedicalHistoryRequest.class));
    }

    @Test
    public void shouldReportPatientActivation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();

        when(requestMapper.map(patient)).thenReturn(request);
        when(allPatients.get(anyString())).thenReturn(patient);
        patientService.activate(patient.getPatientId(), "user");
        verify(patientReportingService).update(eq(request), any(MedicalHistoryRequest.class));
    }

    @Test
    public void shouldReportPatientDeactivation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();

        when(requestMapper.map(patient)).thenReturn(request);
        when(allPatients.get(anyString())).thenReturn(patient);
        patientService.deactivate(patient.getPatientId(), Status.Inactive, "user");
        verify(patientReportingService).update(eq(request), any(MedicalHistoryRequest.class));
    }

    @Test
    public void shouldReportPatientSuspend() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        PatientRequest request = new PatientRequest();

        when(requestMapper.map(patient)).thenReturn(request);
        when(allPatients.get(anyString())).thenReturn(patient);
        patientService.suspend(patient.getPatientId(), "user");
        verify(patientReportingService).update(eq(request), any(MedicalHistoryRequest.class));
    }

    @Test
    public void shouldCreatePatientOnDaily() {
        DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withCallPreference(CallPreference.DailyPillReminder).build();

        patientService.create(patient, "clinicId", USER_NAME);

        verify(allPatients).addToClinic(patient, "clinicId", USER_NAME);
        verify(outbox).enroll(patient);
        final ArgumentCaptor<List> eventLogsCaptor = ArgumentCaptor.forClass(List.class);
        verify(allPatientEventLogs).addAll(eventLogsCaptor.capture());
        final List<PatientEventLog> eventLogs = eventLogsCaptor.getValue();
        assertEquals(1, eventLogs.size());
        assertPatientEventLog(eventLogs.get(0), PatientEvent.Call_Plan_Changed, patient.getPatientPreferences().getCallPreference().name(), now);
    }

    @Test
    public void shouldCreatePatientOnWeekly() {
        DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withCallPreference(CallPreference.FourDayRecall).
                withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).withDayOfWeeklyCall(DayOfWeek.Saturday).build();

        patientService.create(patient, "clinicId", USER_NAME);

        verify(allPatients).addToClinic(patient, "clinicId", USER_NAME);
        verify(outbox).enroll(patient);
        final ArgumentCaptor<List> eventLogsCaptor = ArgumentCaptor.forClass(List.class);
        verify(allPatientEventLogs).addAll(eventLogsCaptor.capture());
        final List<PatientEventLog> eventLogs = eventLogsCaptor.getValue();
        assertEquals(3, eventLogs.size());
        assertPatientEventLog(eventLogs.get(0), PatientEvent.Call_Plan_Changed, patient.getPatientPreferences().getCallPreference().name(), now);
        assertPatientEventLog(eventLogs.get(1), PatientEvent.Day_Of_Weekly_Call_Changed, patient.getPatientPreferences().getDayOfWeeklyCall().name(), now);
        assertPatientEventLog(eventLogs.get(2), PatientEvent.Best_Call_Time_Changed, patient.getPatientPreferences().getBestCallTime().toString(), now);
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
    public void patientChangesHisPreferences() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);
        when(preferenceChangedStrategyFactory.getStrategy(Matchers.<ChangedPatientPreferenceContext>any())).thenReturn(callPlanChangedStrategy);

        patientService.update(patient, USER_NAME);

        verify(callPlanChangedStrategy).execute(dbPatient, patient, currentTreatmentAdvice);
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
    public void patientUpdates_ButDoesNotChangeHisPreferences() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);
        patientService.update(patient, USER_NAME);

        assertNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(allPatients).update(patient, USER_NAME);
        verifyZeroInteractions(preferenceChangedStrategyFactory);
    }

    @Test
    public void updatesOnPatientShouldResultInEventLogs() {
        final DateTime now = new DateTime(2011, 10, 10, 10, 10);
        mockCurrentDate(now);
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_DOC_ID).withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(12, 10, TimeMeridiem.AM)).withDayOfWeeklyCall(DayOfWeek.Sunday).build();
        TreatmentAdvice currentTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(patient.getId())).thenReturn(dbPatient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(currentTreatmentAdvice);
        when(preferenceChangedStrategyFactory.getStrategy(Matchers.<ChangedPatientPreferenceContext>any())).thenReturn(callPlanChangedStrategy);

        patientService.update(patient, USER_NAME);

        verify(allPatients).update(patient, USER_NAME);
        final ArgumentCaptor<List> eventLogsCaptor = ArgumentCaptor.forClass(List.class);
        verify(allPatientEventLogs).addAll(eventLogsCaptor.capture());
        final List<PatientEventLog> eventLogs = eventLogsCaptor.getValue();
        assertEquals(3, eventLogs.size());
        assertPatientEventLog(eventLogs.get(0), PatientEvent.Call_Plan_Changed, patient.getPatientPreferences().getCallPreference().name(), now);
        assertPatientEventLog(eventLogs.get(1), PatientEvent.Day_Of_Weekly_Call_Changed, patient.getPatientPreferences().getDayOfWeeklyCall().name(), now);
        assertPatientEventLog(eventLogs.get(2), PatientEvent.Best_Call_Time_Changed, patient.getPatientPreferences().getBestCallTime().toString(), now);
    }

    @Test
    public void shouldReturnPatientReport() {
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
        when(allRegimens.getBy("regimenId")).thenReturn(regimen);

        PatientReport patientReport = patientService.getPatientReport(patientDocId);

        assertEquals(patient, patientReport.getPatient());
        assertEquals(artStartDate.toDate(), patientReport.getARTStartedOn());
        assertEquals(currentRegimenStartDate.toDate(), patientReport.getCurrentRegimenStartDate());
    }

    @Test
    public void shouldReturnPatientEventLogRelatedToStatusChange() throws Exception {
        PatientEventLog activationEventLog = new PatientEventLog("patientDocId", PatientEvent.Activation);
        PatientEventLog bestCallTimeChangedEventLog = new PatientEventLog("patientDocId", PatientEvent.Best_Call_Time_Changed);
        PatientEventLog callPlanChangedEventLog = new PatientEventLog("patientDocId", PatientEvent.Call_Plan_Changed);
        PatientEventLog suspensionEventLog = new PatientEventLog("patientDocId", PatientEvent.Suspension);
        PatientEventLog tempDeactivationEventLog = new PatientEventLog("patientDocId", PatientEvent.Temporary_Deactivation);

        when(allPatientEventLogs.findByPatientId("patientDocId")).thenReturn(Arrays.asList(activationEventLog, bestCallTimeChangedEventLog,
                callPlanChangedEventLog, suspensionEventLog, tempDeactivationEventLog));

        List<PatientEventLog> patientStatusHistory = patientService.getStatusChangeHistory("patientDocId");

        assertEquals(3, patientStatusHistory.size());
        assertArrayEquals(Arrays.asList(activationEventLog, suspensionEventLog, tempDeactivationEventLog).toArray(), patientStatusHistory.toArray());
    }

    private void assertPatientEventLog(PatientEventLog patientEventLog, PatientEvent patientEvent, String newValue, DateTime now) {
        assertEquals(patientEvent, patientEventLog.getEvent());
        assertEquals(now, patientEventLog.getDate());
        assertEquals(PATIENT_DOC_ID, patientEventLog.getPatientId());
        assertEquals(newValue, patientEventLog.getNewValue());
    }

}
