package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.OutboxModuleStratergy;
import org.motechproject.tama.ivr.context.PillModuleStratergy;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerOutboundCallTest {
    private TAMACallFlowController tamaCallFlowController;
    private PatientPreferences patientPreferences;

    @Before
    public void setUp() {
        TAMATreeRegistry tamaTreeRegistry = mock(TAMATreeRegistry.class);
        PillModuleStratergy pillModuleStratergy = mock(PillModuleStratergy.class);
        OutboxModuleStratergy outboxModuleStratergy = mock(OutboxModuleStratergy.class);
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        AllPatients allPatients = mock(AllPatients.class);

        tamaCallFlowController = new TAMACallFlowController(tamaTreeRegistry, allPatients, contextFactory);
        tamaCallFlowController.registerPillModule(pillModuleStratergy);
        tamaCallFlowController.registerOutboxModule(outboxModuleStratergy);
        TAMAIVRContextForTest tamaIVRContextForTest = new TAMAIVRContextForTest().callDirection(CallDirection.Outbound);

        Patient patient = new Patient();
        patientPreferences = new PatientPreferences();
        patient.setPatientPreferences(patientPreferences);
        when(allPatients.get(null)).thenReturn(patient);
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
