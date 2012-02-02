package org.motechproject.tama.symptomreporting.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.common.util.StringUtil;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(ControllerURLs.DIAL_URL)
public class DialController extends SafeIVRController {

    private AllPatients allPatients;
    private TAMAIVRContextFactory contextFactory;
    private PatientAlertService patientAlertService;
    private SymptomRecordingService symptomRecordingService;

    @Autowired
    public DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, @Qualifier("standardResponseController") StandardResponseController standardResponseController, AllPatients allPatients, PatientAlertService patientAlertService, SymptomRecordingService symptomRecordingService) {
        this(ivrMessage, callDetailRecordsService, standardResponseController, new TAMAIVRContextFactory(), allPatients, patientAlertService, symptomRecordingService);
    }

    protected DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController, TAMAIVRContextFactory contextFactory, AllPatients allPatients, PatientAlertService patientAlertService, SymptomRecordingService symptomRecordingService) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.allPatients = allPatients;
        this.contextFactory = contextFactory;
        this.patientAlertService = patientAlertService;
        this.symptomRecordingService = symptomRecordingService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        return dial(kooKooIVRContext);
    }

    @Override
    public KookooIVRResponseBuilder dial(KooKooIVRContext kooKooIVRContext) {
        symptomRecordingService.setAsNotConnectedToDoctor(kooKooIVRContext.callId());
        TAMAIVRContext tamaivrContext = contextFactory.create(kooKooIVRContext);
        SymptomsReportingContext symptomsReportingContext = new SymptomsReportingContext(kooKooIVRContext);
        Patient patient = allPatients.get(tamaivrContext.patientDocumentId());

        List<Clinic.ClinicianContact> clinicianContacts = patient.getClinic().getClinicianContacts();
        KookooIVRResponseBuilder kookooIVRResponseBuilder = new KookooIVRResponseBuilder().language(tamaivrContext.preferredLanguage());
        if (kooKooIVRContext.isAnswered()) {
            symptomRecordingService.setAsConnectedToDoctor(kooKooIVRContext.callId());
            updateAlertAndEndCurrentCall(tamaivrContext, symptomsReportingContext, clinicianContacts);
        } else {
            tryAndDialTheNextClinician(symptomsReportingContext, clinicianContacts, kookooIVRResponseBuilder);
        }
        return kookooIVRResponseBuilder;
    }

    private void updateAlertAndEndCurrentCall(TAMAIVRContext tamaivrContext, SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts) {
        String clinicianName = clinicianContacts.get(symptomsReportingContext.numberOfCliniciansCalled() - 1).getName();
        patientAlertService.updateDoctorConnectedToDuringSymptomCall(tamaivrContext.patientDocumentId(), clinicianName);
        symptomsReportingContext.endCall();
    }

    private void tryAndDialTheNextClinician(SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts, KookooIVRResponseBuilder kookooIVRResponseBuilder) {
        String nextClinicianPhoneNumber = getNextClinicianPhoneNumber(symptomsReportingContext, clinicianContacts);
        boolean canCallClinician = StringUtils.isNotEmpty(nextClinicianPhoneNumber);
        if (canCallClinician) {
            kookooIVRResponseBuilder.withPlayAudios(TamaIVRMessage.CONNECTING_TO_DOCTOR).withPhoneNumber(StringUtil.ivrMobilePhoneNumber(nextClinicianPhoneNumber));
        } else {
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