package org.motechproject.tama.ivr.controller.callflowcontroller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.context.SymptomModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerInboundCallTest {

    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private PillModuleStrategy pillModuleStrategy;
    @Mock
    private SymptomModuleStrategy symptomModuleStrategy;
    @Mock
    private Cookies cookies;

    private AllPatients allPatients;

    private TAMAIVRContextForTest tamaIVRContextForTest;

    private TAMACallFlowController tamaCallFlowController;

    @Before
    public void setUp() {
        initMocks(this);
        TAMATreeRegistry TAMATreeChooser = mock(TAMATreeRegistry.class);
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        allPatients = mock(AllPatients.class);

        tamaCallFlowController = new TAMACallFlowController(TAMATreeChooser, allPatients, contextFactory);
        tamaCallFlowController.registerPillModule(pillModuleStrategy);
        tamaCallFlowController.registerOutboxModule();
        tamaCallFlowController.registerSymptomModule(symptomModuleStrategy);
        setupContext(contextFactory);
    }

    private void setupContext(TAMAIVRContextFactory contextFactory) {
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
        tamaIVRContextForTest = new TAMAIVRContextForTest(kooKooIVRContext).callDirection(CallDirection.Inbound);
        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaIVRContextForTest);
    }

    @Test
    public void currentDosageTakeTreeShouldBeReturnedWhenPatientHasTakenTheDosage() {
        Patient patient = Mockito.mock(Patient.class);
        PatientPreferences patientPreferences = Mockito.mock(PatientPreferences.class);
        when(allPatients.get(null)).thenReturn(patient);

        when(patient.getStatus()).thenReturn(Status.Active);
        when(patient.getPatientPreferences()).thenReturn(patientPreferences);
        when(patient.isOnDailyPillReminder()).thenReturn(true);
        when(pillModuleStrategy.isCurrentDoseTaken(tamaIVRContextForTest)).thenReturn(true);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void currentDosageConfirmTreeShouldBeReturnedWhenPatientHasNotTakenTheDosage() {
        Patient patient = Mockito.mock(Patient.class);
        PatientPreferences patientPreferences = Mockito.mock(PatientPreferences.class);
        when(allPatients.get(null)).thenReturn(patient);

        when(patient.getStatus()).thenReturn(Status.Active);
        when(patient.getPatientPreferences()).thenReturn(patientPreferences);
        when(patient.isOnDailyPillReminder()).thenReturn(true);
        when(pillModuleStrategy.isCurrentDoseTaken(tamaIVRContextForTest)).thenReturn(false);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void whenCallStateIsSymptomReportingTree() {
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);
        tamaIVRContextForTest.callState(CallState.SYMPTOM_REPORTING_TREE);
        assertEquals(AllIVRURLs.DECISION_TREE_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
        assertEquals(TAMATreeRegistry.REGIMEN_1_TO_6, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void whenCallStateIsOutbox() {
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);
        tamaIVRContextForTest.callState(CallState.OUTBOX);
        assertEquals(ControllerURLs.OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void whenCallStateIsSymptomReporting() {
        tamaIVRContextForTest.callState(CallState.SYMPTOM_REPORTING);
        assertEquals(ControllerURLs.SYMPTOM_REPORTING_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldPushMessagesWhenAllTreesAreCompleteAndNoMessageWasPushedAndPatientIsOnDailyPillReminder() {
        String patientDocumentId = "patientDocumentId";
        Patient patientNotOnDailyPillReminder = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();

        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);
        tamaIVRContextForTest.patientDocumentId(patientDocumentId);
        tamaIVRContextForTest.setMessagesPushed(false);

        when(allPatients.get(patientDocumentId)).thenReturn(patientNotOnDailyPillReminder);

        assertEquals(ControllerURLs.PUSH_MESSAGES_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldNotPushMessageWhenPatientNotOnDailyPillReminderOnIncomingCall() {
        String patientDocumentId = "patientDocumentId";
        Patient patientNotOnDailyPillReminder = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();

        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);
        tamaIVRContextForTest.setMessagesPushed(false);
        tamaIVRContextForTest.patientDocumentId(patientDocumentId);
        tamaIVRContextForTest.callDirection(CallDirection.Inbound);

        when(allPatients.get(patientDocumentId)).thenReturn(patientNotOnDailyPillReminder);

        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void whenSymptomReportingTreeIsComplete() {
        tamaIVRContextForTest.setMessagesPushed(true);
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.REGIMEN_1_TO_6);
        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);

        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
        verify(cookies).add(TAMAIVRContext.DO_NOT_PROMPT_FOR_HANG_UP, "true");
    }
}
