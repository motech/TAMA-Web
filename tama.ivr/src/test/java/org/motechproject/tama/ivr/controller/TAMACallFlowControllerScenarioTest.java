package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.PillModuleStrategy;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerScenarioTest {
    @Mock
    private PillModuleStrategy pillModuleStrategy;
    @Mock
    private OutboxModuleStrategy outboxModuleStrategy;
    @Mock
    private TAMAIVRContextFactory contextFactory;
    @Mock
    private AllPatients allPatients;
    @Mock
    private KooKooIVRContext kooKooIVRContext;

    private TAMACallFlowController callFlowController;
    private TAMAIVRContextForTest ivrContext;
    private PatientPreferences patientPreferences;

    @Before
    public void setUp() {
        initMocks(this);
        TAMATreeRegistry treeRegistry = new TAMATreeRegistry();
        callFlowController = new TAMACallFlowController(treeRegistry, allPatients, contextFactory);
        callFlowController.registerPillModule(pillModuleStrategy);
        callFlowController.registerOutboxModule(outboxModuleStrategy);
        ivrContext = new TAMAIVRContextForTest();
        Patient patient = new Patient();
        patientPreferences = new PatientPreferences();
        patient.setPatientPreferences(patientPreferences);
        ivrContext.callState(CallState.STARTED);

        when(allPatients.get(null)).thenReturn(patient);
        when(contextFactory.create(kooKooIVRContext)).thenReturn(ivrContext);
    }

    private void callPreference(CallPreference callPreference) {
        patientPreferences.setCallPreference(callPreference);
    }

    private void callDirection(CallDirection callDirection) {
        ivrContext.callDirection(callDirection);
    }

    private void currentDosageTaken(boolean taken) {
        when(pillModuleStrategy.isCurrentDoseTaken(ivrContext)).thenReturn(taken);
    }

    private void previousDosageCaptured(boolean captured) {
        when(pillModuleStrategy.previousDosageCaptured(ivrContext)).thenReturn(captured);
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

    @Test
    public void OnPillReminder_OutgoingCall_CurrentDosageNotTaken__NotFeelingWell() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Outbound);
        currentDosageTaken(false);
        previousDosageCaptured(true);

        assertURL(ControllerURLs.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);

        callState(CallState.SYMPTOM_REPORTING);
        assertURL(ControllerURLs.SYMPTOM_REPORTING_URL);

        callState(CallState.SYMPTOM_REPORTING_TREE);
        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.REGIMEN_1_TO_6);

        treeComplete(TAMATreeRegistry.REGIMEN_1_TO_6);
        when(outboxModuleStrategy.shouldContinueToOutbox(any(String.class))).thenReturn(true);
        assertURL(ControllerURLs.PRE_OUTBOX_URL);
    }

    @Test
    public void OnPillReminder_OutgoingCall_CurrentDosageNotTaken_PreviousDosageCaptured__ConfirmingCurrentDosage() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Outbound);
        currentDosageTaken(false);
        previousDosageCaptured(true);

        assertURL(ControllerURLs.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);

        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        when(outboxModuleStrategy.shouldContinueToOutbox(any(String.class))).thenReturn(true);
        assertURL(ControllerURLs.PRE_OUTBOX_URL);
    }

    @Test
    public void OnPillReminder_OutgoingCall_CurrentDosageNotTaken_PreviousDosageNotCaptured() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Outbound);
        currentDosageTaken(false);
        previousDosageCaptured(false);

        assertURL(ControllerURLs.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);

        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER);

        treeComplete(TAMATreeRegistry.PREVIOUS_DOSAGE_REMINDER);
        when(outboxModuleStrategy.shouldContinueToOutbox(any(String.class))).thenReturn(true);
        assertURL(ControllerURLs.PRE_OUTBOX_URL);
    }

    @Test
    public void OnPillReminder_IncomingCall_CurrentDosageTaken() {
        callPreference(CallPreference.DailyPillReminder);
        callDirection(CallDirection.Inbound);
        currentDosageTaken(true);

        assertURL(ControllerURLs.AUTHENTICATION_URL);
        callState(CallState.AUTHENTICATED);
        assertURL(AllIVRURLs.DECISION_TREE_URL);
        assertTree(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);

        treeComplete(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);
        callState(CallState.OUTBOX);
        assertURL(ControllerURLs.OUTBOX_URL);

        when(outboxModuleStrategy.hasOutboxCompleted(ivrContext)).thenReturn(true);
        assertURL(ControllerURLs.MENU_REPEAT);
    }
}
