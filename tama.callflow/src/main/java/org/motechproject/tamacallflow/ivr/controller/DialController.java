package org.motechproject.tamacallflow.ivr.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tamacommon.util.StringUtil;
import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.SymptomsReportingContext;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(TAMACallFlowController.DIAL_URL)
public class DialController extends SafeIVRController {

    private AllPatients allPatients;
    private TAMAIVRContextFactory contextFactory;
    private PatientAlertService patientAlertService;

    @Autowired
    public DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, @Qualifier("standardResponseController") StandardResponseController standardResponseController, AllPatients allPatients, PatientAlertService patientAlertService) {
        this(ivrMessage, callDetailRecordsService, standardResponseController, new TAMAIVRContextFactory(), patientAlertService);
        this.allPatients = allPatients;
    }

    protected DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController, TAMAIVRContextFactory contextFactory, PatientAlertService patientAlertService) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.contextFactory = contextFactory;
        this.patientAlertService = patientAlertService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        return dial(kooKooIVRContext);
    }

    @Override
    public KookooIVRResponseBuilder dial(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = contextFactory.create(kooKooIVRContext);
        SymptomsReportingContext symptomsReportingContext = contextFactory.createSymptomReportingContext(kooKooIVRContext);

        List<Clinic.ClinicianContact> clinicianContacts = tamaivrContext.patient(allPatients).getClinic().getClinicianContacts();
        KookooIVRResponseBuilder kookooIVRResponseBuilder = new KookooIVRResponseBuilder().language(tamaivrContext.preferredLanguage());
        if (kooKooIVRContext.isAnswered()) {
            updateAlertAndEndCurrentCall(tamaivrContext, symptomsReportingContext, clinicianContacts);
        }
        else {
            tryAndDialTheNextClinician(symptomsReportingContext, clinicianContacts, kookooIVRResponseBuilder);
        }
        return kookooIVRResponseBuilder;
    }

    private void updateAlertAndEndCurrentCall(TAMAIVRContext tamaivrContext, SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts) {
        String clinicianName = clinicianContacts.get(symptomsReportingContext.numberOfCliniciansCalled() - 1).getName();
        patientAlertService.updateDoctorConnectedToDuringSymptomCall(tamaivrContext.patientId(), clinicianName);
        symptomsReportingContext.endCall();
    }

    private void tryAndDialTheNextClinician(SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts, KookooIVRResponseBuilder kookooIVRResponseBuilder) {
        String nextClinicianPhoneNumber = getNextClinicianPhoneNumber(symptomsReportingContext, clinicianContacts);
        boolean canCallClinician = StringUtils.isNotEmpty(nextClinicianPhoneNumber);
        if (canCallClinician){
            kookooIVRResponseBuilder.withPlayAudios(TamaIVRMessage.CONNECTING_TO_DOCTOR).withPhoneNumber(StringUtil.ivrMobilePhoneNumber(nextClinicianPhoneNumber));
        }
        else {
            kookooIVRResponseBuilder.withPlayAudios(TamaIVRMessage.CANNOT_CONNECT_TO_DOCTOR);
            symptomsReportingContext.endCall();
        }
    }

    private String getNextClinicianPhoneNumber(SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts) {
        for (int numberOfClincianBeingCalled = symptomsReportingContext.anotherClinicianCalled();
             numberOfClincianBeingCalled <= clinicianContacts.size();
             numberOfClincianBeingCalled = symptomsReportingContext.anotherClinicianCalled()) {
            String clinicianPhoneNumber = clinicianContacts.get(numberOfClincianBeingCalled - 1).getPhoneNumber();
            if (StringUtils.isEmpty(clinicianPhoneNumber)) {
                continue;
            }
            return clinicianPhoneNumber;
        }
        return StringUtils.EMPTY;
    }
}