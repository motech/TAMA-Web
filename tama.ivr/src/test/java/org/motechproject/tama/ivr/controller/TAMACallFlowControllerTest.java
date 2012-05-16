package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.context.SymptomModuleStrategy;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerTest {
    @Mock
    private TAMATreeRegistry treeRegistry;
    @Mock
    private PillModuleStrategy pillModuleStrategy;
    @Mock
    private OutboxModuleStrategy outboxModuleStrategy;
    @Mock
    private TAMAIVRContextFactory contextFactory;
    @Mock
    private AllPatients allPatients;
    @Mock
    private SymptomModuleStrategy symptomModuleStrategy;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    private TAMACallFlowController tamaCallFlowController;
    private TAMAIVRContextForTest tamaIVRContext;

    @Before
    public void setUp() {
        initMocks(this);
        tamaCallFlowController = new TAMACallFlowController(treeRegistry, allPatients, contextFactory);
        tamaCallFlowController.registerPillModule(pillModuleStrategy);
        tamaCallFlowController.registerOutboxModule(outboxModuleStrategy);
        tamaCallFlowController.registerSymptomModule(symptomModuleStrategy);
        tamaIVRContext = new TAMAIVRContextForTest();
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
        String patientId = "1234";
        tamaIVRContext.patientDocumentId(patientId);
        when(outboxModuleStrategy.shouldContinueToOutbox(patientId)).thenReturn(true);
        assertEquals(ControllerURLs.PRE_OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void menuRepeatURLShouldBeReturnedWhenThereAreNoMessagesInOutbox() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        tamaIVRContext.patientDocumentId(patientId);
        when(outboxModuleStrategy.shouldContinueToOutbox(patientId)).thenReturn(false);
        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnHealthTipURLWhenCallStateIsHealthTip() {
        tamaIVRContext.callState(CallState.HEALTH_TIPS);
        assertEquals(ControllerURLs.HEALTH_TIPS_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnHealthTipURLWhenCallState_AndOutboxHasNotCompleted() {
        when(outboxModuleStrategy.hasOutboxCompleted(tamaIVRContext)).thenReturn(true);
        tamaIVRContext.callState(CallState.HEALTH_TIPS);
        assertEquals(ControllerURLs.HEALTH_TIPS_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void dialPromptsShouldLeadToDialURL() {
        tamaIVRContext.isDialState(true).callState(CallState.SYMPTOM_REPORTING);
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
    public void completionOfOutboxShouldLeadToMenuRepeat() {
        tamaIVRContext.callState(CallState.OUTBOX);
        when(outboxModuleStrategy.hasOutboxCompleted(tamaIVRContext)).thenReturn(true);
        String patientId = "1234";
        tamaIVRContext.patientDocumentId(patientId);
        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
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
    public void shouldReturnMenuTreeWhenPatientIsSuspendedAndIsOnDailyPillReminder() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withStatus(Status.Suspended).build());
        tamaIVRContext.callDirection(CallDirection.Inbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldReturnFourDayRecallIncomingTreeWhenPatientIsSuspendedButOnWeeklyAdherence() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(allPatients.get(null)).thenReturn(PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).withCallPreference(CallPreference.FourDayRecall).build());
        tamaIVRContext.callDirection(CallDirection.Inbound);

        assertEquals(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToMenuRepetitionOnceAllTreesAreCompletedAndThereAreNoOutboxMessages() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        when(outboxModuleStrategy.shouldContinueToOutbox(any(String.class))).thenReturn(false);

        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
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
        tamaIVRContext.callState(CallState.END_OF_FLOW);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(ControllerURLs.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }
}
