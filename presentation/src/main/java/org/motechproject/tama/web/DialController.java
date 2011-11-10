package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class DialController {

    private AllPatients allPatients;
    private IVRMessage ivrMessages;

    @Autowired
    protected DialController(AllPatients allPatients, IVRMessage ivrMessages) {
        this.allPatients = allPatients;
        this.ivrMessages = ivrMessages;
    }

    @RequestMapping(TAMACallFlowController.DIAL_URL)
    @ResponseBody
    public String dial(@ModelAttribute KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(new KooKooIVRContext(kooKooRequest, request, response));
        List<Clinic.ClinicianContact> clinicianContacts = tamaivrContext.patient(allPatients).getClinic().getClinicianContacts();
        KookooIVRResponseBuilder kookooIVRResponseBuilder = new KookooIVRResponseBuilder();
        kookooIVRResponseBuilder.withPhoneNumber(clinicianContacts.get(0).getPhoneNumber());
        return kookooIVRResponseBuilder.create(ivrMessages);
    }

}