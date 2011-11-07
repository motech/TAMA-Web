package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientPreferences;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerOutboundCallTest {
    private TAMACallFlowController tamaCallFlowController;
    private PatientPreferences patientPreferences;

    @Before
    public void setUp() {
        TAMATreeRegistry tamaTreeRegistry = mock(TAMATreeRegistry.class);
        PillReminderService pillReminderService = mock(PillReminderService.class);
        VoiceOutboxService voiceOutboxService = mock(VoiceOutboxService.class);
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        AllPatients allPatients = mock(AllPatients.class);

        tamaCallFlowController = new TAMACallFlowController(tamaTreeRegistry, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        TAMAIVRContextForTest tamaIVRContextForTest = new TAMAIVRContextForTest().callDirection(CallDirection.Outbound);

        Patient patient = new Patient();
        patientPreferences = new PatientPreferences();
        patient.setPatientPreferences(patientPreferences);
        tamaIVRContextForTest.patient(patient);
        when(contextFactory.create(Matchers.<KooKooIVRContext>any())).thenReturn(tamaIVRContextForTest);
    }

    @Test
    public void fourDayRecallTreeShouldBeReturnedWhenItsAnIncomingCallAndPatientPreferenceIsForWeeklyAdherence() {
        patientPreferences.setCallPreference(CallPreference.FourDayRecall);

        String treeName = tamaCallFlowController.decisionTreeName(null);
        assertEquals(TAMATreeRegistry.FOUR_DAY_RECALL, treeName);
    }

    @Test
    public void currentDosageReminderTreeShouldBeReturnedWhenTAMACallsPatient() {
        patientPreferences.setCallPreference(CallPreference.DailyPillReminder);
        String treeName = tamaCallFlowController.decisionTreeName(null);
        assertEquals(TAMATreeRegistry.CURRENT_DOSAGE_REMINDER, treeName);
    }
}
