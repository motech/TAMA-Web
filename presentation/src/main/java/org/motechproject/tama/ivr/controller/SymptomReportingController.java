package org.motechproject.tama.ivr.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.service.PatientService;
import org.motechproject.tama.service.SymptomReportingTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.SYMPTOM_REPORTING_URL)
public class SymptomReportingController extends SafeIVRController {
    private TAMAIVRContextFactory contextFactory;
    private PatientService patientService;
    private SymptomReportingTreeService symptomReportingTreeService;

    @Autowired
    protected SymptomReportingController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, PatientService patientService, SymptomReportingTreeService symptomReportingTreeService, StandardResponseController standardResponseController) {
        this(ivrMessage, callDetailRecordsService, new TAMAIVRContextFactory(), patientService, symptomReportingTreeService, standardResponseController);
    }

    public SymptomReportingController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, TAMAIVRContextFactory contextFactory, PatientService patientService, SymptomReportingTreeService symptomReportingTreeService,
                                      StandardResponseController standardResponseController) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.contextFactory = contextFactory;
        this.patientService = patientService;
        this.symptomReportingTreeService = symptomReportingTreeService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaIvrContext = contextFactory.create(kooKooIVRContext);
        String callId = tamaIvrContext.callId();
        String patientId = tamaIvrContext.patientId();

        MedicalCondition medicalCondition = patientService.getPatientMedicalConditions(patientId);
        String symptomReportingTree = symptomReportingTreeService.getSymptomReportingTree(medicalCondition);
        tamaIvrContext.symptomReportingTree(symptomReportingTree);
        tamaIvrContext.callState(CallState.SYMPTOM_REPORTING_TREE);

        return KookooResponseFactory.empty(callId);
    }
}