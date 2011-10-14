package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
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
    private TAMAIVRContextForTest tamaIVRContextForTest;

    @Before
    public void setUp() {
        initMocks(this);
        tamaCallFlowController = new TAMACallFlowController(treeRegistry, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        tamaIVRContextForTest = new TAMAIVRContextForTest();
        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaIVRContextForTest);
    }

    @Test
    public void outboxURLShouldBeReturnedWhenTheDecisionTreesAreComplete() {
        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        tamaIVRContextForTest.patientId(patientId);
        when(voiceOutboxService.getNumberPendingMessages(patientId)).thenReturn(3);
        assertEquals(TAMACallFlowController.PRE_OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test(expected = TamaException.class)
    public void hangupURLShouldBeReturnedWhenThereAreNoMessagesInOutbox() {
        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);
        String patientId = "1234";
        tamaIVRContextForTest.patientId(patientId);
        when(voiceOutboxService.getNumberPendingMessages(patientId)).thenReturn(0);
        tamaCallFlowController.urlFor(kooKooIVRContext);
    }

    @Test
    public void returnAuthenticationURLWhenTheCallStarts() {
        tamaIVRContextForTest.callState(CallState.STARTED);
        assertEquals(TAMACallFlowController.AUTHENTICATION_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void completionOfSymptomReportingTreeShouldCompleteTheTrees() {
        when(treeRegistry.isLeafTree(TAMATreeRegistry.REGIMEN_1_TO_6)).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.REGIMEN_1_TO_6, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, tamaIVRContextForTest.callState());
    }

    @Test
    public void completionOfRemindersShouldCompleteTheTrees() {
        PillRegimenSnapshot pillRegimenSnapshot = mock(PillRegimenSnapshot.class);
        tamaIVRContextForTest.pillRegimenSnapshot(pillRegimenSnapshot);
        when(pillRegimenSnapshot.isPreviousDosageCaptured()).thenReturn(true);
        tamaCallFlowController.treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, tamaIVRContextForTest.callState());
    }
}
