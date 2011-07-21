package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class NewCallEventAction extends BaseAction {

    private Patients patients;
    private UserNotFoundAction userNotFoundAction;

    @Autowired
    public NewCallEventAction(IVRMessage messages, Patients patients, UserNotFoundAction userNotFoundAction) {
        this.patients = patients;
        this.userNotFoundAction = userNotFoundAction;
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        Patient patient = patients.findByMobileNumber(ivrRequest.getCid());
        if (!isValidCaller(patient))
            return userNotFoundAction.handle(ivrRequest, request, response);

        HttpSession session = request.getSession();
        session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
        session.setAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID, patient.getId());
        return dtmfResponseWithWav(ivrRequest, IVRMessage.TAMA_SIGNATURE_MUSIC_URL);
    }

    private boolean isValidCaller(Patient patient) {
        return (patient != null && patient.isActive());
    }
}
