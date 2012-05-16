package org.motechproject.tama.symptomreporting.controller;

import org.motechproject.ivr.domain.IVRMessage;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomReportingAlertService;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.motechproject.tama.symptomsreporting.decisiontree.service.SymptomReportingTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerURLs.SYMPTOM_REPORTING_URL)
public class SymptomReportingController extends SafeIVRController {
    private TAMAIVRContextFactory contextFactory;
    private SymptomReportingService symptomReportingService;
    private SymptomReportingTreeService symptomReportingTreeService;
    private SymptomReportingAlertService symptomReportingAlertService;

    @Autowired
    protected SymptomReportingController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, SymptomReportingService symptomReportingService,
                                         SymptomReportingTreeService symptomReportingTreeService, StandardResponseController standardResponseController, SymptomReportingAlertService symptomReportingAlertService) {
        this(ivrMessage, callDetailRecordsService, new TAMAIVRContextFactory(), symptomReportingService, symptomReportingTreeService, standardResponseController, symptomReportingAlertService);
    }

    public SymptomReportingController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, TAMAIVRContextFactory contextFactory, SymptomReportingService symptomReportingService,
                                      SymptomReportingTreeService symptomReportingTreeService, StandardResponseController standardResponseController, SymptomReportingAlertService symptomReportingAlertService) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.contextFactory = contextFactory;
        this.symptomReportingService = symptomReportingService;
        this.symptomReportingTreeService = symptomReportingTreeService;
        this.symptomReportingAlertService = symptomReportingAlertService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaIvrContext = contextFactory.create(kooKooIVRContext);
        String callId = tamaIvrContext.callId();
        String patientId = tamaIvrContext.patientDocumentId();

        MedicalCondition medicalCondition = symptomReportingService.getPatientMedicalConditions(patientId);
        String symptomReportingTree = symptomReportingTreeService.parseRulesAndFetchTree(medicalCondition);
        tamaIvrContext.symptomReportingTree(symptomReportingTree);
        tamaIvrContext.callState(CallState.SYMPTOM_REPORTING_TREE);

        symptomReportingAlertService.createSymptomsReportingAlert(tamaIvrContext.patientDocumentId());

        return KookooResponseFactory.empty(callId);
    }

}