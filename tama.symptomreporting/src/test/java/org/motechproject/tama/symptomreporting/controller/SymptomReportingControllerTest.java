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
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.motechproject.tama.symptomsreporting.decisiontree.service.SymptomReportingTreeService;

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
    private SymptomReportingTreeService symptomReportingTreeService;
    @Mock
    private SymptomReportingService symptomReportingService;
    @Mock
    private KooKooIVRContext kookooIvrContext;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private PatientAlertService patientAlertService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldHandleDTMFEventForSymptomReportingFlow() {
        SymptomReportingController symptomReportingController = new SymptomReportingController(ivrMessage, callDetailRecordsService, contextFactory, symptomReportingService, symptomReportingTreeService, standardResponseController, patientAlertService);

        String callId = "123";
        String patientId = "patientId";
        TAMAIVRContextForTest tamaivrContext = new TAMAIVRContextForTest();
        tamaivrContext.patientDocumentId(patientId).callId(callId);

        String symptomReportingTree = "RegimenTree";
        MedicalCondition medicalCondition = new MedicalCondition();
        medicalCondition.regimenName("RegimenName");
        medicalCondition.age(20);

        when(contextFactory.create(kookooIvrContext)).thenReturn(tamaivrContext);
        when(symptomReportingService.getPatientMedicalConditions(patientId)).thenReturn(medicalCondition);
        when(symptomReportingTreeService.parseRulesAndFetchTree(medicalCondition)).thenReturn(symptomReportingTree);

        KookooIVRResponseBuilder kookooIVRResponseBuilder = symptomReportingController.gotDTMF(kookooIvrContext);
        assertFalse(kookooIVRResponseBuilder.isCollectDTMF());

        verify(symptomReportingService).getPatientMedicalConditions(patientId);
        verify(symptomReportingTreeService).parseRulesAndFetchTree(medicalCondition);
        verify(patientAlertService).createSymptomsReportingAlert(patientId);

        assertEquals(symptomReportingTree, tamaivrContext.symptomReportingTree());
        assertEquals(CallState.SYMPTOM_REPORTING_TREE, tamaivrContext.callState());
    }
}