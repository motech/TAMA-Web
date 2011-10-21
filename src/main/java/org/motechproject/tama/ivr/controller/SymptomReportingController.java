package org.motechproject.tama.ivr.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.domain.PatientMedicalConditions;
import org.motechproject.tama.ivr.CallState;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.service.PatientService;
import org.motechproject.tama.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(TAMACallFlowController.SYMPTOM_REPORTING_URL)
public class SymptomReportingController extends SafeIVRController {
    private TAMAIVRContextFactory contextFactory;
    private PatientService patientService;
    private SymptomReportingService symptomReportingService;

    @Autowired
    protected SymptomReportingController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, PatientService patientService, SymptomReportingService symptomReportingService) {
        this(ivrMessage, callDetailRecordsService, new TAMAIVRContextFactory(), patientService, symptomReportingService);
    }

    public SymptomReportingController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, TAMAIVRContextFactory contextFactory, PatientService patientService, SymptomReportingService symptomReportingService) {
        super(ivrMessage, callDetailRecordsService);
        this.contextFactory = contextFactory;
        this.patientService = patientService;
        this.symptomReportingService = symptomReportingService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaIvrContext = contextFactory.create(kooKooIVRContext);
        String callId = tamaIvrContext.callId();
        String patientId = tamaIvrContext.patientId();

        PatientMedicalConditions patientMedicalConditions = patientService.getPatientMedicalConditions(patientId);
        String symptomReportingTree = symptomReportingService.getSymptomReportingTree(patientMedicalConditions);
        tamaIvrContext.symptomReportingTree(symptomReportingTree);
        tamaIvrContext.callState(CallState.SYMPTOM_REPORTING_TREE);

        return KookooResponseFactory.empty(callId);
    }
}