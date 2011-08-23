package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class NewCallEventAction extends BaseIncomingAction {
    private Patients patients;
    private UserNotFoundAction userNotFoundAction;

    @Autowired
    public NewCallEventAction(IVRMessage messages, Patients patients, UserNotFoundAction userNotFoundAction) {
        this.messages = messages;
        this.patients = patients;
        this.userNotFoundAction = userNotFoundAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = createIVRSession(request);
        Patient patient = patients.findByMobileNumber(ivrRequest.getCid());
        if (!isValidCaller(patient)) {
            return userNotFoundAction.handle(ivrRequest, request, response);
        }
        ivrSession.setState(IVRCallState.COLLECT_PIN);
        ivrSession.set(IVRCallAttribute.PATIENT_DOC_ID, patient.getId());
        ivrSession.set(IVRCallAttribute.PREFERRED_LANGUAGE_CODE, patient.getIvrLanguage().getCode());
        return dtmfResponseWithWav(ivrRequest, ivrSession, IVRMessage.SIGNATURE_MUSIC_URL);
    }

    private boolean isValidCaller(Patient patient) {
        return (patient != null && patient.isActive());
    }
}
