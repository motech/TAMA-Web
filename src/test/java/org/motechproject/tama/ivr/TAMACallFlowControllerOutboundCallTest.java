package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientPreferences;
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
        TAMATreeRegistry TAMATreeChooser = mock(TAMATreeRegistry.class);
        PillReminderService pillReminderService = mock(PillReminderService.class);
        VoiceOutboxService voiceOutboxService = mock(VoiceOutboxService.class);
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        AllPatients allPatients = mock(AllPatients.class);

        tamaCallFlowController = new TAMACallFlowController(TAMATreeChooser, pillReminderService, voiceOutboxService, allPatients, contextFactory);
        TAMAIVRContextForTest tamaIVRContextForTest = new TAMAIVRContextForTest().callDirection(CallDirection.Outbound);

        Patient patient = new Patient();
        patientPreferences = new PatientPreferences();
        patient.setPatientPreferences(patientPreferences);
        tamaIVRContextForTest.patient(patient);
        when(contextFactory.create(null)).thenReturn(tamaIVRContextForTest);
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
