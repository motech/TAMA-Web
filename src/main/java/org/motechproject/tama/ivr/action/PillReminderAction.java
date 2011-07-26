package org.motechproject.tama.ivr.action;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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
        String id = (String) session.getAttribute(IVR.Attributes.PATIENT_DOC_ID);
        Patient patient = patients.get(id);
        Clinic clinic = clinics.get(patient.getClinic_id());

        audit(ivrRequest, id, IVRCallAudit.State.USER_AUTHORISED);
        return medicinesAndMenu(ivrRequest, clinic);
    }

    private String medicinesAndMenu(IVRRequest ivrRequest, Clinic clinic) {
        IVRResponseBuilder builder = new IVRResponseBuilder();
        builder.withSid(ivrRequest.getSid()).addPlayAudio(messages.getWav(clinic.getName()));

        for (String medicine : medicines(ivrRequest))   {
            builder.addPlayAudio(messages.getWav(IVRMessage.YOU_ARE_SUPPOSED_TO_TAKE));
            builder.addPlayAudio(messages.getWav(medicine));
        }
        String playMenu = messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU);
        builder.withCollectDtmf(new IVRDtmfBuilder().withPlayAudio(playMenu).create());
        return builder.create().getXML();
    }

    private List<String> medicines(IVRRequest ivrRequest) {
        Map<String, String> params = ivrRequest.getTamaParams();
        String regimen = params.get(PillReminderCall.REGIMEN_ID);
        String dosage = params.get(PillReminderCall.DOSAGE_ID);
        return service.medicinesFor(regimen, dosage);
    }

}
