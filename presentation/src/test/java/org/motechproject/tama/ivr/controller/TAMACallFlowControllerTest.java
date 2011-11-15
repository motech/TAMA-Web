package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Status;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.context.SymptomsReportingContext;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
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
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        tamaCallFlowController = new TAMACallFlowController(treeRegistry, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        ivrContext = new TAMAIVRContextForTest();
        when(contextFactory.create(kooKooIVRContext)).thenReturn(ivrContext);
        when(contextFactory.createSymptomReportingContext(kooKooIVRContext)).thenReturn(symptomsReportingContext);
    }

    @Test
    public void outboxURLShouldBeReturnedWhenTheDecisionTreesAreComplete() {
        ivrContext.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        ivrContext.patientId(patientId);
        when(voiceOutboxService.getNumberPendingMessages(patientId)).thenReturn(3);
        assertEquals(TAMACallFlowController.PRE_OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void hangupURLShouldBeReturnedWhenThereAreNoMessagesInOutbox() {
        ivrContext.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        ivrContext.patientId(patientId);
        when(voiceOutboxService.getNumberPendingMessages(patientId)).thenReturn(0);
        assertEquals(TAMACallFlowController.HANG_UP_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void returnAuthenticationURLWhenTheCallStarts() {
        ivrContext.callState(CallState.STARTED);
        assertEquals(TAMACallFlowController.AUTHENTICATION_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void dialPromptsShouldLeadToDialURL() {
        ivrContext.callState(CallState.SYMPTOM_REPORTING);
        when(symptomsReportingContext.isDialState()).thenReturn(true);
        assertEquals(TAMACallFlowController.DIAL_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void completionOfSymptomReportingTreeOrPreviousDosageReminderTreeShouldCompleteTheTrees() {
        when(treeRegistry.isLeafTree(TAMATreeRegistry.REGIMEN_1_TO_6)).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, ivrContext.callState());
    }

    @Test
    public void whenCurrentDosageIsConfirmedAndWhenPreviousDosageHasBeenCaptured_AllTreesAreCompleted() {
        PillRegimenSnapshot pillRegimenSnapshot = mock(PillRegimenSnapshot.class);
        ivrContext.pillRegimenSnapshot(pillRegimenSnapshot);
        ivrContext.callState(CallState.AUTHENTICATED);
        when(pillRegimenSnapshot.isPreviousDosageCaptured()).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, ivrContext.callState());
    }

    @Test
    public void completionOfOutboxShouldLeadToHangup() {
        ivrContext.callState(CallState.OUTBOX);
        ivrContext.outboxCompleted(true);
        String patientId = "1234";
        ivrContext.patientId(patientId);
        assertEquals(TAMACallFlowController.HANG_UP_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void shouldReturnSymptomReportingTreeBasedOnCallState() {
        ivrContext.callState(CallState.SYMPTOM_REPORTING_TREE);
        String symptomReportingTreeName = "Regimen1_1";
        ivrContext.symptomReportingTree(symptomReportingTreeName);

        tamaCallFlowController.getTree(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);

        verify(treeRegistry).getSymptomReportingTree(TAMATreeRegistry.REGIMEN_1_TO_6, symptomReportingTreeName);
    }

    @Test
    public void shouldReturnDecisionTree() {
        ivrContext.callState(CallState.AUTHENTICATED);
        tamaCallFlowController.getTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, kooKooIVRContext);

        verify(treeRegistry).getTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
    }

    @Test
    public void shouldReturnMenuTreeWhenPatientIsSuspendedAndIsOnDailyPillReminder() {
        ivrContext.callState(CallState.AUTHENTICATED);
        ivrContext.patient(PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).build());
        ivrContext.callDirection(CallDirection.Inbound);
        final String treeName = tamaCallFlowController.decisionTreeName(kooKooIVRContext);

        assertEquals(TAMATreeRegistry.MENU_TREE, treeName);
    }

    @Test
    public void shouldReturnFourDayRecallIncomingTreeWhenPatientIsSuspendedButOnWeeklyAdherence() {
        ivrContext.callState(CallState.AUTHENTICATED);
        ivrContext.patient(PatientBuilder.startRecording().withDefaults().withStatus(Status.Suspended).withCallPreference(CallPreference.FourDayRecall).build());
        ivrContext.callDirection(CallDirection.Inbound);
        final String treeName = tamaCallFlowController.decisionTreeName(kooKooIVRContext);

        assertEquals(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL, treeName);
    }
}
