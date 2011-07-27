package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
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
import java.util.List;
import java.util.Map;

@Service
public class DoseRemindAction extends BaseIncomingAction {
    public static final String KEY = "0";
    private Patients patients;
    private Clinics clinics;
    private PillReminderService service;

    @Autowired
    public DoseRemindAction(Patients patients, Clinics clinics, PillReminderService service, IVRMessage messages, IVRCallAudits audits) {
        this.patients = patients;
        this.clinics = clinics;
        this.service = service;
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        ivrSession.setState(IVRCallState.COLLECT_DOSE_RESPONSE);

        Patient patient = patients.get(ivrSession.getPatientId());
        Clinic clinic = clinics.get(patient.getClinic_id());

        IVRResponseBuilder builder = new IVRResponseBuilder().withSid(ivrRequest.getSid());
        builder.addPlayAudio(messages.getWav(clinic.getName()));

        for (String medicine : getMedicines(ivrRequest))
            builder.addPlayAudio(
                    messages.getWav(IVRMessage.YOU_ARE_SUPPOSED_TO_TAKE),
                    messages.getWav(medicine));

        builder.withCollectDtmf(new IVRDtmfBuilder().withPlayAudio(messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU)).create());
        return builder.create().getXML();
    }

    private List<String> getMedicines(IVRRequest ivrRequest) {
        Map<String, String> params = ivrRequest.getTamaParams();
        String regimen = params.get(PillReminderCall.REGIMEN_ID);
        String dosage = params.get(PillReminderCall.DOSAGE_ID);
        return service.medicinesFor(regimen, dosage);
    }

    @Override
    public String getKey() {
        return KEY;
    }

}


