package org.motechproject.tama.symptomreporting.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.symptomreporting.criteria.ContinueToSymptomsTreeCriteria;
import org.motechproject.tama.symptomreporting.service.SymptomReportingAlertService;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.motechproject.tama.symptomsreporting.decisiontree.service.SymptomReportingTreeService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingControllerTest {

    @Mock
    private KooKooIVRContext kookooIvrContext;
    private TAMAIVRContextForTest tamaivrContext;

    @Mock
    private SymptomReportingTreeService symptomReportingTreeService;
    @Mock
    private SymptomReportingService symptomReportingService;
    @Mock
    private SymptomReportingAlertService symptomReportingAlertService;
    @Mock
    private ContinueToSymptomsTreeCriteria continueToSymptomsTreeCriteria;

    private SymptomReportingController symptomReportingController;

    @Before
    public void setUp() {
        initMocks(this);

        IVRMessage ivrMessage = mock(IVRMessage.class);

        tamaivrContext = new TAMAIVRContextForTest();
        TAMAIVRContextFactory contextFactory = setupCallContext();

        KookooCallDetailRecordsService callDetailRecordsService = mock(KookooCallDetailRecordsService.class);

        StandardResponseController standardResponseController = mock(StandardResponseController.class);

        symptomReportingController = new SymptomReportingController(ivrMessage,
                callDetailRecordsService,
                contextFactory,
                symptomReportingService,
                symptomReportingTreeService,
                standardResponseController,
                symptomReportingAlertService,
                continueToSymptomsTreeCriteria
        );
    }

    private TAMAIVRContextFactory setupCallContext() {
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);
        when(contextFactory.create(kookooIvrContext)).thenReturn(tamaivrContext);
        return contextFactory;
    }

    @Test
    public void shouldHandleDTMFEventForSymptomReportingFlow() {
        String callId = "123";
        String patientId = "patientId";

        tamaivrContext.patientDocumentId(patientId).callId(callId);

        String symptomReportingTree = "RegimenTree";
        MedicalCondition medicalCondition = new MedicalCondition();
        medicalCondition.regimenName("RegimenName");
        medicalCondition.age(20);

        when(continueToSymptomsTreeCriteria.shouldContinue(patientId)).thenReturn(true);
        when(symptomReportingService.getPatientMedicalConditions(patientId)).thenReturn(medicalCondition);
        when(symptomReportingTreeService.parseRulesAndFetchTree(medicalCondition)).thenReturn(symptomReportingTree);

        KookooIVRResponseBuilder kookooIVRResponseBuilder = symptomReportingController.gotDTMF(kookooIvrContext);
        assertFalse(kookooIVRResponseBuilder.isCollectDTMF());

        verify(symptomReportingService).getPatientMedicalConditions(patientId);
        verify(symptomReportingTreeService).parseRulesAndFetchTree(medicalCondition);
        verify(symptomReportingAlertService).createSymptomsReportingAlert(patientId);

        assertEquals(symptomReportingTree, tamaivrContext.symptomReportingTree());
        assertEquals(CallState.SYMPTOM_REPORTING_TREE, tamaivrContext.callState());
    }

    @Test
    public void shouldHangupWhenContinueToSymptomsTreeCriteriaIsNotSatisfied() {
        String callId = "123";
        String patientId = "patientId";

        tamaivrContext.patientDocumentId(patientId).callId(callId);

        when(continueToSymptomsTreeCriteria.shouldContinue(patientId)).thenReturn(false);

        KookooIVRResponseBuilder result = symptomReportingController.gotDTMF(kookooIvrContext);
        assertEquals(CallState.END_OF_FLOW, tamaivrContext.callState());
        assertTrue(result.isEmpty());
    }
}