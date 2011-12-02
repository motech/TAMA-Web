package org.motechproject.tamacallflow.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.CallPreference;
import org.motechproject.tamadomain.domain.Status;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.context.SymptomsReportingContext;
import org.motechproject.tamacallflow.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerTest {
    @Mock
    private TAMATreeRegistry treeRegistry;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private VoiceOutboxService voiceOutboxService;
    @Mock
    private TAMAIVRContextFactory contextFactory;
    @Mock
    private AllPatients allPatients;
    @Mock
    private SymptomsReportingContext symptomsReportingContext;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    private TAMACallFlowController tamaCallFlowController;
    private TAMAIVRContextForTest tamaIVRContext;

    @Before
    public void setUp() {
        initMocks(this);
        tamaCallFlowController = new TAMACallFlowController(treeRegistry, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        tamaIVRContext = new TAMAIVRContextForTest();
        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaIVRContext);
        when(contextFactory.createSymptomReportingContext(kooKooIVRContext)).thenReturn(symptomsReportingContext);
    }


    @Test
    public void returnAuthenticationURLWhenTheCallStarts() {
        tamaIVRContext.callState(CallState.STARTED);
        assertEquals(TAMACallFlowController.AUTHENTICATION_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void outboxURLShouldBeReturnedWhenTheDecisionTreesAreComplete() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        tamaIVRContext.patientId(patientId);
        when(voiceOutboxService.getNumberPendingMessages(patientId)).thenReturn(3);
        assertEquals(TAMACallFlowController.PRE_OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void menuRepeatURLShouldBeReturnedWhenThereAreNoMessagesInOutbox() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        tamaIVRContext.patientId(patientId);
        when(voiceOutboxService.getNumberPendingMessages(patientId)).thenReturn(0);
        assertEquals(TAMACallFlowController.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnHealthTipURLWhenCallStateIsHealthTip() {
        tamaIVRContext.callState(CallState.HEALTH_TIPS);
        assertEquals(TAMACallFlowController.HEALTH_TIPS_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnHealthTipURLWhenCallState_AndOutboxHasNotCompleted() {
        tamaIVRContext.outboxCompleted(true);
        tamaIVRContext.callState(CallState.HEALTH_TIPS);
        assertEquals(TAMACallFlowController.HEALTH_TIPS_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void dialPromptsShouldLeadToDialURL() {
        tamaIVRContext.callState(CallState.SYMPTOM_REPORTING);
        when(symptomsReportingContext.isDialState()).thenReturn(true);
        assertEquals(TAMACallFlowController.DIAL_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void completionOfSymptomReportingTreeOrPreviousDosageReminderTreeShouldCompleteTheTrees() {
        when(treeRegistry.isLeafTree(TAMATreeRegistry.REGIMEN_1_TO_6)).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, tamaIVRContext.callState());
    }

    @Test
    public void whenCurrentDosageIsConfirmedAndWhenPreviousDosageHasBeenCaptured_AllTreesAreCompleted() {
        PillRegimenSnapshot pillRegimenSnapshot = mock(PillRegimenSnapshot.class);
        tamaIVRContext.pillRegimenSnapshot(pillRegimenSnapshot);
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        when(pillRegimenSnapshot.isPreviousDosageCaptured()).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, tamaIVRContext.callState());
    }

    @Test
    public void completionOfOutboxShouldLeadToMenuRepeat() {
        tamaIVRContext.callState(CallState.OUTBOX);
        tamaIVRContext.outboxCompleted(true);
        String patientId = "1234";
        tamaIVRContext.patientId(patientId);
        assertEquals(TAMACallFlowController.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnSymptomReportingTreeBasedOnCallState() {
        tamaIVRContext.callState(CallState.SYMPTOM_REPORTING_TREE);
        String symptomReportingTreeName = "Regimen1_1";
        tamaIVRContext.symptomReportingTree(symptomReportingTreeName);

        tamaCallFlowController.getTree(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);

        verify(treeRegistry).getSymptomReportingTree(TAMATreeRegistry.REGIMEN_1_TO_6, symptomReportingTreeName);
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
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build());
        tamaIVRContext.callDirection(CallDirection.Inbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldReturnFourDayRecallIncomingTreeWhenPatientIsSuspendedButOnWeeklyAdherence() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).withCallPreference(CallPreference.FourDayRecall).build());
        tamaIVRContext.callDirection(CallDirection.Inbound);

        assertEquals(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToMenuRepetitionOnceAllTreesAreCompletedAndThereAreNoOutboxMessages() {
        tamaIVRContext.callState(CallState.ALL_TREES_COMPLETED);
        when(voiceOutboxService.getNumberPendingMessages(any(String.class))).thenReturn(0);

        assertEquals(TAMACallFlowController.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldNotTransitionToCurrentDosageReminderTreeIsPlayed_AndUserEntersWillTakeLater() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToMenuTreeOnceFourDayRecallTreeIsPlayed() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build());
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.FOUR_DAY_RECALL);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldNotRepeatOutboxCallTreeMoreThanOnceDuringOutboxCall() {
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.OUTBOX_CALL);
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void shouldTransitionToCurrentDosageReminderTreeForPatientOnDailyPillReminder(){
        tamaIVRContext.callState(CallState.AUTHENTICATED);
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }
    
    @Test
    public void shouldTransitionToMenuRepeatWhenCallStateIs_EndOfFlow(){
        tamaIVRContext.callState(CallState.END_OF_FLOW);
        tamaIVRContext.patient(PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build());
        tamaIVRContext.callDirection(CallDirection.Outbound);

        assertEquals(TAMACallFlowController.MENU_REPEAT, tamaCallFlowController.urlFor(kooKooIVRContext));
    }
}
