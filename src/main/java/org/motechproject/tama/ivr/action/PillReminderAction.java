package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class PillReminderAction extends BaseIncomingAction {
    private Patients patients;
    private Clinics clinics;
    private PillReminderService service;

    @Autowired
    public PillReminderAction(IVRMessage messages, Patients patients, Clinics clinics, IVRCallAudits audits, PillReminderService service) {
        this.patients = patients;
        this.clinics = clinics;
        this.service = service;
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String id = (String) session.getAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID);

        Patient patient = patients.get(id);
        Clinic clinic = clinics.get(patient.getClinic_id());
        audits.add(new IVRCallAudit(ivrRequest.getCid(), ivrRequest.getSid(), id, IVRCallAudit.State.USER_AUTHORISED));

        String playText = StringUtils.replace(messages.get(IVRMessage.TAMA_IVR_WELCOME_MESSAGE), "{0}", clinic.getName());
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withPlayText(playText).create();




        return ivrResponse.getXML();
    }

}
