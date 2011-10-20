package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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
    private KooKooIVRContext kooKooIVRContext;
    private TAMACallFlowController tamaCallFlowController;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        tamaCallFlowController = new TAMACallFlowController(treeRegistry, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        ivrContext = new TAMAIVRContextForTest();
        when(contextFactory.create(kooKooIVRContext)).thenReturn(ivrContext);
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
}
