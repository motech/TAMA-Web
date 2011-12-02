package org.motechproject.tamacallflow.ivr.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tama.ivr.decisiontree.service.SymptomReportingTreeService;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.service.PatientService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingControllerTest {
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private TAMAIVRContextFactory contextFactory;
    @Mock
    private PatientService patientService;
    @Mock
    private SymptomReportingTreeService symptomReportingService;
    @Mock
    private KooKooIVRContext kookooIvrContext;
    @Mock
    private StandardResponseController standardResponseController;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldHandleDTMFEventForSymptomReportingFlow() {
        SymptomReportingController symptomReportingController = new SymptomReportingController(ivrMessage, callDetailRecordsService, contextFactory, patientService, symptomReportingService, standardResponseController);

        String callId = "123";
        String patientId = "patientId";
        TAMAIVRContextForTest tamaivrContext = new TAMAIVRContextForTest().callId(callId).patientId(patientId);

        String symptomReportingTree = "RegimenTree";
        MedicalCondition medicalCondition = new MedicalCondition();
        medicalCondition.regimenName("RegimenName");
        medicalCondition.age(20);

        when(contextFactory.create(kookooIvrContext)).thenReturn(tamaivrContext);
        when(patientService.getPatientMedicalConditions(patientId)).thenReturn(medicalCondition);
        when(symptomReportingService.parseRulesAndFetchTree(medicalCondition)).thenReturn(symptomReportingTree);

        KookooIVRResponseBuilder kookooIVRResponseBuilder = symptomReportingController.gotDTMF(kookooIvrContext);
        assertFalse(kookooIVRResponseBuilder.isCollectDTMF());

        verify(patientService).getPatientMedicalConditions(patientId);
        verify(symptomReportingService).parseRulesAndFetchTree(medicalCondition);
        assertEquals(symptomReportingTree, tamaivrContext.symptomReportingTree());
        assertEquals(CallState.SYMPTOM_REPORTING_TREE, tamaivrContext.callState());
    }
}