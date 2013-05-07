package org.motechproject.tama.ivr.controller.callflowcontroller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.context.SymptomModuleStrategy;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerTest {
    @Mock
    private TAMATreeRegistry treeRegistry;
    @Mock
    private PillModuleStrategy pillModuleStrategy;
    @Mock
    private TAMAIVRContextFactory contextFactory;
    @Mock
    private AllPatients allPatients;
    @Mock
    private SymptomModuleStrategy symptomModuleStrategy;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private Cookies cookies;

    private TAMAIVRContextForTest tamaIVRContext;

    private TAMACallFlowController tamaCallFlowController;

    @Before
    public void setUp() {
        initMocks(this);
        tamaCallFlowController = new TAMACallFlowController(treeRegistry, allPatients, contextFactory);
        tamaCallFlowController.registerPillModule(pillModuleStrategy);
        tamaCallFlowController.registerOutboxModule();
        tamaCallFlowController.registerSymptomModule(symptomModuleStrategy);

        when(kooKooIVRContext.cookies()).thenReturn(cookies);
        tamaIVRContext = new TAMAIVRContextForTest(kooKooIVRContext);
        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaIVRContext);
    }

    @Test
    public void returnAuthenticationURLWhenTheCallStarts() {
        tamaIVRContext.callState(CallState.STARTED);
        assertEquals(ControllerURLs.AUTHENTICATION_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void outboxURLShouldBeReturnedWhenTheDecisionTreesAreComplete() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        tamaIVRContext.callDirection(CallDirection.Outbound);
        String patientId = "1234";
        tamaIVRContext.patientDocumentId(patientId);

        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(CallPreference.DailyPillReminder).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        assertEquals(ControllerURLs.PUSH_MESSAGES_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnDecisionTreeURLWhenCallStateIsPullMessages() {
        tamaIVRContext.callState(CallState.PULL_MESSAGES_TREE);
        assertEquals(AllIVRURLs.DECISION_TREE_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void menuRepeatURLShouldBeReturnedAfterPushOfMessages() {
        tamaIVRContext.callState(CallState.PUSH_MESSAGES_COMPLETE);
        String patientId = "1234";
        tamaIVRContext.patientDocumentId(patientId);
        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void dialPromptsShouldLeadToDialURL() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        tamaIVRContext.isDialState(true);
        assertEquals(ControllerURLs.DIAL_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void completionOfSymptomReportingTreeOrPreviousDosageReminderTreeShouldCompleteTheTrees() {
        when(treeRegistry.isLeafTree(TAMATreeRegistry.REGIMEN_1_TO_6)).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, tamaIVRContext.callState());
    }

    @Test
    public void whenCurrentDosageIsConfirmedAndWhenPreviousDosageHasBeenCaptured_AllTreesAreCompleted() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(pillModuleStrategy.previousDosageCaptured(tamaIVRContext)).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, tamaIVRContext.callState());
    }

    @Test
    public void shouldReturnSymptomReportingTreeBasedOnCallState() {
        tamaIVRContext.callState(CallState.SYMPTOM_REPORTING_TREE);
        String symptomReportingTreeName = "Regimen1_1";
        tamaIVRContext.symptomReportingTree(symptomReportingTreeName);

        tamaCallFlowController.getTree(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);

        verify(symptomModuleStrategy).getTree("Regimen_1_To_6", tamaIVRContext);
    }

    @Test
    public void shouldReturnDecisionTree() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        tamaCallFlowController.getTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, kooKooIVRContext);

        verify(treeRegistry).getTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
    }

    @Test
    public void shouldReturnPullMessagesTreeWhenCallStateIsPullMessages() {
        tamaIVRContext.callState(CallState.PULL_MESSAGES_TREE);
        tamaIVRContext.callDirection(CallDirection.Inbound);
        tamaIVRContext.lastCompletedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);

        when(allPatients.get(anyString())).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());

        String treeName = tamaCallFlowController.decisionTreeName(kooKooIVRContext);
        assertEquals(TAMATreeRegistry.PULL_MESSAGES_TREE, treeName);
    }

    @Test
    public void shouldReturnMenuTreeWhenPatientIsSuspendedAndIsOnDailyPillReminder() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withStatus(Status.Suspended).build());
        tamaIVRContext.callDirection(CallDirection.Inbound);

        assertEquals(TAMATreeRegistry.INCOMING_MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldReturnFourDayRecallIncomingTreeWhenPatientIsSuspendedButOnWeeklyAdherence() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).withCallPreference(CallPreference.FourDayRecall).build());
        tamaIVRContext.callDirection(CallDirection.Inbound);

        assertEquals(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldNotTransitionToCurrentDosageReminderTreeIsPlayed_AndUserEntersWillTakeLater() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToMenuTreeOnceFourDayRecallTreeIsPlayed() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build());
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.FOUR_DAY_RECALL);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldNotRepeatOutboxCallTreeMoreThanOnceDuringOutboxCall() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.OUTBOX_CALL);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToCurrentDosageReminderTreeForPatientOnDailyPillReminder() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToMenuRepeatWhenCallStateIs_EndOfFlow() {
        tamaIVRContext.callState(CallState.PUSH_MESSAGES_COMPLETE);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }
}
