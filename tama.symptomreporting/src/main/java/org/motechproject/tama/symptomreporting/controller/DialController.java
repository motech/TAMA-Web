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
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;
import org.motechproject.tama.symptomreporting.factory.SymptomReportingContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(ControllerURLs.DIAL_URL)
public class DialController extends SafeIVRController {

    private AllPatients allPatients;
    private SymptomReportingContextFactory contextFactory;
    private PatientAlertService patientAlertService;
    private SymptomRecordingService symptomRecordingService;
    private SymptomReportingService symptomReportingService;

    @Autowired
    public DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, @Qualifier("standardResponseController") StandardResponseController standardResponseController, AllPatients allPatients, PatientAlertService patientAlertService, SymptomRecordingService symptomRecordingService, SymptomReportingService symptomReportingService) {
        this(ivrMessage, callDetailRecordsService, standardResponseController, new SymptomReportingContextFactory(), allPatients, patientAlertService, symptomRecordingService, symptomReportingService);
    }

    protected DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController, SymptomReportingContextFactory contextFactory, AllPatients allPatients, PatientAlertService patientAlertService, SymptomRecordingService symptomRecordingService, SymptomReportingService symptomReportingService) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.allPatients = allPatients;
        this.contextFactory = contextFactory;
        this.patientAlertService = patientAlertService;
        this.symptomRecordingService = symptomRecordingService;
        this.symptomReportingService = symptomReportingService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        return dial(kooKooIVRContext);
    }

    @Override
    public KookooIVRResponseBuilder dial(KooKooIVRContext kooKooIVRContext) {
        SymptomsReportingContext symptomsReportingContext = contextFactory.create(kooKooIVRContext);
        Patient patient = allPatients.get(symptomsReportingContext.patientDocumentId());

        List<Clinic.ClinicianContact> clinicianContacts = patient.getClinic().getClinicianContacts();
        KookooIVRResponseBuilder kookooIVRResponseBuilder = new KookooIVRResponseBuilder().language(symptomsReportingContext.preferredLanguage());
        if (symptomsReportingContext.isAnswered()) {
            symptomRecordingService.setAsConnectedToDoctor(symptomsReportingContext.callId());
            updateAlertAndEndCurrentCall(symptomsReportingContext, symptomsReportingContext, clinicianContacts);
        } else {
            symptomRecordingService.setAsNotConnectedToDoctor(symptomsReportingContext.callId());
            tryAndDialTheNextClinician(symptomsReportingContext, clinicianContacts, kookooIVRResponseBuilder);
        }
        return kookooIVRResponseBuilder;
    }

    private void updateAlertAndEndCurrentCall(SymptomsReportingContext context, SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts) {
        String clinicianName = clinicianContacts.get(symptomsReportingContext.numberOfCliniciansCalled() - 1).getName();
        patientAlertService.updateDoctorConnectedToDuringSymptomCall(context.patientDocumentId(), clinicianName);
        symptomsReportingContext.endCall();
    }

    private void tryAndDialTheNextClinician(SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts, KookooIVRResponseBuilder kookooIVRResponseBuilder) {
        String nextClinicianPhoneNumber = getNextClinicianPhoneNumber(symptomsReportingContext, clinicianContacts);
        boolean canCallClinician = StringUtils.isNotEmpty(nextClinicianPhoneNumber);
        if (canCallClinician) {
            symptomReportingService.smsOTCAdviceToClinician(symptomsReportingContext, nextClinicianPhoneNumber);
            kookooIVRResponseBuilder.withPlayAudios(TamaIVRMessage.CONNECTING_TO_DOCTOR).withPhoneNumber(StringUtil.ivrMobilePhoneNumber(nextClinicianPhoneNumber));
        } else {
            kookooIVRResponseBuilder.withPlayAudios(TamaIVRMessage.CANNOT_CONNECT_TO_DOCTOR);
            symptomsReportingContext.endCall();
        }
    }

    private String getNextClinicianPhoneNumber(SymptomsReportingContext symptomsReportingContext, List<Clinic.ClinicianContact> clinicianContacts) {
        for (int numberOfClinicianBeingCalled = symptomsReportingContext.anotherClinicianCalled();
             numberOfClinicianBeingCalled <= clinicianContacts.size();
             numberOfClinicianBeingCalled = symptomsReportingContext.anotherClinicianCalled()) {
            String clinicianPhoneNumber = clinicianContacts.get(numberOfClinicianBeingCalled - 1).getPhoneNumber();
            if (StringUtils.isEmpty(clinicianPhoneNumber)) {
                continue;
            }
            return clinicianPhoneNumber;
        }
        return StringUtils.EMPTY;
    }
}