package org.motechproject.tama.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.ivr.SymptomsReportingContextWrapper;
import org.motechproject.tama.ivr.SymptomsReportingContextWrapperFactory;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(TAMACallFlowController.DIAL_URL)
public class DialController extends SafeIVRController {

    private AllPatients allPatients;
    private SymptomsReportingContextWrapperFactory symptomsReportingContextFactory;

    @Autowired
    public DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController, AllPatients allPatients) {
        this(ivrMessage, callDetailRecordsService, standardResponseController);
        this.allPatients = allPatients;
        this.symptomsReportingContextFactory = new SymptomsReportingContextWrapperFactory();
    }

    protected DialController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        SymptomsReportingContextWrapper symptomsReportingContextWrapper = symptomsReportingContextFactory.create(kooKooIVRContext);

        List<Clinic.ClinicianContact> clinicianContacts = tamaivrContext.patient(allPatients).getClinic().getClinicianContacts();
        KookooIVRResponseBuilder kookooIVRResponseBuilder = new KookooIVRResponseBuilder();
        if (kooKooIVRContext.isAnswered()) {
            symptomsReportingContextWrapper.endCall();
        }
        else {
            tryAndDialTheNextClinician(symptomsReportingContextWrapper, clinicianContacts, kookooIVRResponseBuilder);
        }
        return kookooIVRResponseBuilder;
    }

    private void tryAndDialTheNextClinician(SymptomsReportingContextWrapper symptomsReportingContextWrapper, List<Clinic.ClinicianContact> clinicianContacts, KookooIVRResponseBuilder kookooIVRResponseBuilder) {
        String nextClinicianPhoneNumber = getNextClinicianPhoneNumber(symptomsReportingContextWrapper, clinicianContacts);
        String lastClinicianPhoneNumber = clinicianContacts.get(clinicianContacts.size() - 1).getPhoneNumber();
        kookooIVRResponseBuilder.withPhoneNumber(StringUtil.ivrMobilePhoneNumber(nextClinicianPhoneNumber));
        if (lastClinicianPhoneNumber.equals(nextClinicianPhoneNumber)){
            symptomsReportingContextWrapper.endCall();
        }
    }

    private String getNextClinicianPhoneNumber(SymptomsReportingContextWrapper symptomsReportingContextWrapper, List<Clinic.ClinicianContact> clinicianContacts) {
        int numberOfClincianBeingCalled = symptomsReportingContextWrapper.anotherClinicianCalled();
        for (; numberOfClincianBeingCalled <= clinicianContacts.size(); numberOfClincianBeingCalled ++) {
            String clinicianPhoneNumber = clinicianContacts.get(numberOfClincianBeingCalled - 1).getPhoneNumber();
            if (StringUtils.isEmpty(clinicianPhoneNumber)) {
                continue;
            }
            return clinicianPhoneNumber;
        }
        return StringUtils.EMPTY;
    }
}