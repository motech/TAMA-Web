package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.AllIVRURLs;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientPreferences;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.SymptomsReportingContext;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerInboundCallTest {
    private TAMACallFlowController tamaCallFlowController;
    private TAMAIVRContextForTest tamaIVRContextForTest;
    private PillRegimenSnapshot pillRegimenSnapshot;

    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private VoiceOutboxService voiceOutboxService;
    @Mock
    private SymptomsReportingContext symptomsReportingContext;

    @Before
    public void setUp() {
        initMocks(this);
        TAMATreeRegistry TAMATreeChooser = mock(TAMATreeRegistry.class);
        PillReminderService pillReminderService = mock(PillReminderService.class);
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        AllPatients allPatients = mock(AllPatients.class);


        tamaCallFlowController = new TAMACallFlowController(TAMATreeChooser, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        tamaIVRContextForTest = new TAMAIVRContextForTest().callDirection(CallDirection.Inbound);
        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaIVRContextForTest);
        when(contextFactory.createSymptomReportingContext(kooKooIVRContext)).thenReturn(symptomsReportingContext);
    }

    @Test
    public void currentDosageTakeTreeShouldBeReturnedWhenPatientHasTakenTheDosage() {
        pillRegimenSnapshot = mock(PillRegimenSnapshot.class);
        Patient patient = Mockito.mock(Patient.class);
        PatientPreferences patientPreferences = Mockito.mock(PatientPreferences.class);
        tamaIVRContextForTest.pillRegimenSnapshot(pillRegimenSnapshot);
        tamaIVRContextForTest.patient(patient);

        when(patient.getPatientPreferences()).thenReturn(patientPreferences);
        when(patientPreferences.getCallPreference()).thenReturn(CallPreference.DailyPillReminder);
        when(pillRegimenSnapshot.isCurrentDosageTaken()).thenReturn(true);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void currentDosageConfirmTreeShouldBeReturnedWhenPatientHasNotTakenTheDosage() {
        Patient patient = Mockito.mock(Patient.class);
        PatientPreferences patientPreferences = Mockito.mock(PatientPreferences.class);
        pillRegimenSnapshot = mock(PillRegimenSnapshot.class);
        tamaIVRContextForTest.patient(patient);
        tamaIVRContextForTest.pillRegimenSnapshot(pillRegimenSnapshot);

        when(patient.getPatientPreferences()).thenReturn(patientPreferences);
        when(patientPreferences.getCallPreference()).thenReturn(CallPreference.DailyPillReminder);
        when(pillRegimenSnapshot.isCurrentDosageTaken()).thenReturn(false);

        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void whenCallStateIsSymptomReportingTree() {
        tamaIVRContextForTest.pillRegimenSnapshot(pillRegimenSnapshot);
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);
        tamaIVRContextForTest.callState(CallState.SYMPTOM_REPORTING_TREE);
        assertEquals(AllIVRURLs.DECISION_TREE_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
        assertEquals(TAMATreeRegistry.REGIMEN_1_TO_6, tamaCallFlowController.decisionTreeName(kooKooIVRContext));
    }

    @Test
    public void whenCallStateIsOutbox() {
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);
        tamaIVRContextForTest.callState(CallState.OUTBOX);
        assertEquals(TAMACallFlowController.OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void whenCallStateIsSymptomReporting() {
        tamaIVRContextForTest.callState(CallState.SYMPTOM_REPORTING);
        assertEquals(TAMACallFlowController.SYMPTOM_REPORTING_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }

    @Test
    public void whenSymptomReportingTreeIsComplete() {
        when(voiceOutboxService.getNumberPendingMessages(any(String.class))).thenReturn(1);
        tamaIVRContextForTest.lastCompletedTree(TAMATreeRegistry.REGIMEN_1_TO_6);
        tamaIVRContextForTest.callState(CallState.ALL_TREES_COMPLETED);
        assertEquals(TAMACallFlowController.PRE_OUTBOX_URL, tamaCallFlowController.urlFor(kooKooIVRContext));
    }
}
