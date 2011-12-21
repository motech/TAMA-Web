package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.OutboxModuleStratergy;
import org.motechproject.tama.ivr.context.PillModuleStratergy;
import org.motechproject.tama.ivr.context.SymptomModuleStratergy;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerInboundCallTest {
    private TAMACallFlowController tamaCallFlowController;
    private TAMAIVRContextForTest tamaIVRContextForTest;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private PillModuleStratergy pillModuleStratergy;
    @Mock
    private OutboxModuleStratergy outboxModuleStratergy;
    @Mock
    private SymptomModuleStratergy symptomModuleStratergy;
    private AllPatients allPatients;

    @Before
    public void setUp() {
        initMocks(this);
        TAMATreeRegistry TAMATreeChooser = mock(TAMATreeRegistry.class);
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        allPatients = mock(AllPatients.class);

        tamaCallFlowController = new TAMACallFlowController(TAMATreeChooser, allPatients, contextFactory);
        tamaCallFlowController.registerPillModule(pillModuleStratergy);
        tamaCallFlowController.registerOutboxModule(outboxModuleStratergy);
        tamaCallFlowController.registerSymptomModule(symptomModuleStratergy);
        tamaIVRContextForTest = new TAMAIVRContextForTest().callDirection(CallDirection.Inbound);
        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaIVRContextForTest);
    }

    @Test
    public void currentDosageTakeTreeShouldBeReturnedWhenPatientHasTakenTheDosage() {
        Patient patient = Mockito.mock(Patient.class);
        PatientPreferences patientPreferences = Mockito.mock(PatientPreferences.class);
        when(allPatients.get(null)).thenReturn(patient);

        when(patient.getStatus()).thenReturn(Status.Active);
        when(patient.getPatientPreferences()).thenReturn(patientPreferences);
        when(patientPreferences.getCallPreference()).thenReturn(CallPreference.DailyPillReminder);
        when(pillModuleStratergy.isCurrentDoseTaken(tamaIVRContextForTest)).thenReturn(true);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void currentDosageConfirmTreeShouldBeReturnedWhenPatientHasNotTakenTheDosage() {
        Patient patient = Mockito.mock(Patient.class);
        PatientPreferences patientPreferences = Mockito.mock(PatientPreferences.class);
        when(allPatients.get(null)).thenReturn(patient);

        when(patient.getStatus()).thenReturn(Status.Active);
        when(patient.getPatientPreferences()).thenReturn(patientPreferences);
        when(patientPreferences.getCallPreference()).thenReturn(CallPreference.DailyPillReminder);
        when(pillModuleStratergy.isCurrentDoseTaken(tamaIVRContextForTest)).thenReturn(false);

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
    public void whenSymptomReportingTreeIsComplete() {
        when(outboxModuleStratergy.getNumberPendingMessages(any(String.class))).thenReturn(1);
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.REGIMEN_1_TO_6);
        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);
        assertEquals(ControllerURLs.PRE_OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }
}