package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientPreferences;
import org.motechproject.tama.ivr.context.SymptomsReportingContext;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerScenarioTest {
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
    @Mock
    private SymptomsReportingContext symptomsReportingContext;
    @Mock
    private PillRegimenSnapshot pillRegimenSnapshot;

    private TAMACallFlowController callFlowController;
    private TAMAIVRContextForTest ivrContext;
    private PatientPreferences patientPreferences;

    @Before
    public void setUp() {
        initMocks(this);
        TAMATreeRegistry treeRegistry = new TAMATreeRegistry(null, null, null, null, null, null, null, null, null);
        callFlowController = new TAMACallFlowController(treeRegistry, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        ivrContext = new TAMAIVRContextForTest();
        Patient patient = new Patient();
        patientPreferences = new PatientPreferences();
        patient.setPatientPreferences(patientPreferences);
        ivrContext.pillRegimenSnapshot(pillRegimenSnapshot).patient(patient).callState(CallState.STARTED);
        when(contextFactory.create(kooKooIVRContext)).thenReturn(ivrContext);
        when(contextFactory.createSymptomReportingContext(kooKooIVRContext)).thenReturn(symptomsReportingContext);
    }

    private void callPreference(CallPreference callPreference) {
        patientPreferences.setCallPreference(callPreference);
    }

    private void callDirection(CallDirection callDirection) {
        ivrContext.callDirection(callDirection);
    }

    private void currentDosageTaken(boolean taken) {
        when(pillRegimenSnapshot.isCurrentDosageTaken()).thenReturn(taken);
    }

    private void previousDosageCaptured(boolean captured) {
        when(pillRegimenSnapshot.isPreviousDosageCaptured()).thenReturn(captured);
    }

    private void callState(CallState callState) {
        ivrContext.callState(callState);
    }

    private void assertURL(String url) {
        assertEquals(url, callFlowController.urlFor(kooKooIVRContext));
    }

    private void assertTree(String treeName) {
        assertEquals(treeName, callFlowController.decisionTreeName(kooKooIVRContext));
    }

    private void treeComplete(String tree) {
        callFlowController.treeComplete(tree, kooKooIVRContext);
    }

    private void messagesInOutbox(int number) {
        when(voiceOutboxService.getNumberPendingMessages(any(String.class))).thenReturn(number);
    }

    @Test
    public void OnPillReminder_OutgoingCall_CurrentDosageNotTaken__NotFeelingWell() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Outbound);
        currentDosageTaken(false);
        previousDosageCaptured(true);

        assertURL(TAMACallFlowController.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);

        callState(CallState.SYMPTOM_REPORTING);
        assertURL(TAMACallFlowController.SYMPTOM_REPORTING_URL);

        callState(CallState.SYMPTOM_REPORTING_TREE);
        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.REGIMEN_1_TO_6);

        treeComplete(TAMATreeRegistry.REGIMEN_1_TO_6);
        messagesInOutbox(1);
        assertURL(TAMACallFlowController.PRE_OUTBOX_URL);
    }

    @Test
    public void OnPillReminder_OutgoingCall_CurrentDosageNotTaken_PreviousDosageCaptured__ConfirmingCurrentDosage() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Outbound);
        currentDosageTaken(false);
        previousDosageCaptured(true);

        assertURL(TAMACallFlowController.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);

        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        messagesInOutbox(1);
        assertURL(TAMACallFlowController.PRE_OUTBOX_URL);
    }

    @Test
    public void OnPillReminder_OutgoingCall_CurrentDosageNotTaken_PreviousDosageNotCaptured() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Outbound);
        currentDosageTaken(false);
        previousDosageCaptured(false);

        assertURL(TAMACallFlowController.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);

        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER);

        treeComplete(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER);
        messagesInOutbox(1);
        assertURL(TAMACallFlowController.PRE_OUTBOX_URL);
    }

    @Test
    public void OnPillReminder_IncomingCall_CurrentDosageTaken() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Inbound);
        currentDosageTaken(true);

        assertURL(TAMACallFlowController.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);

        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);
        callState(CallState.OUTBOX);
        assertURL(TAMACallFlowController.OUTBOX_URL);

        ivrContext.outboxCompleted(true);
        assertURL(TAMACallFlowController.MENU_REPEAT);
    }
}
