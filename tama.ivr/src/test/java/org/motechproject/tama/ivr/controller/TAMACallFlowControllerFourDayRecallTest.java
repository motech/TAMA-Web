package org.motechproject.tama.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TAMACallFlowControllerFourDayRecallTest {

    @Mock
    private AllPatients allPatients;

    private TAMACallFlowController tamaCallFlowController;

    private TAMAIVRContextForTest tamaIVRContext;

    @Mock
    private TAMAIVRContextFactory factory;

    private void setUpPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        when(allPatients.get(patient.getId())).thenReturn(patient);
    }

    private void setUpOutgoingCall() {
        tamaIVRContext.callDirection(CallDirection.Outbound);
    }

    @Before
    public void setUp() {
        initMocks(this);
        tamaIVRContext = new TAMAIVRContextForTest();
        setUpPatient();
        when(factory.create(null)).thenReturn(tamaIVRContext);
        tamaCallFlowController = new TAMACallFlowController(new TAMATreeRegistry(), allPatients, factory);
    }

    @Test
    public void decisionTreeNameTreeShouldBeMenuTreeAfterTraversingFourDayRecallIncomingCallTree() {
        setUpOutgoingCall();
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.FOUR_DAY_RECALL);
        assertEquals(TAMATreeRegistry.MENU_TREE, tamaCallFlowController.decisionTreeName(null));
    }
}
