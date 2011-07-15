package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class AuthenticateAction extends BaseAction {
    @Autowired
    private Patients patients;
    @Autowired
    private RetryAction retryAction;
    @Autowired
    private UserNotFoundAction userNotFoundAction;
    @Autowired
    private UserContinueAction userContinueAction;

    public AuthenticateAction() {
    }

    public AuthenticateAction(Patients patients, RetryAction retryAction, UserNotFoundAction userNotFoundAction, UserContinueAction userContinueAction) {
        this.patients = patients;
        this.retryAction = retryAction;
        this.userNotFoundAction = userNotFoundAction;
        this.userContinueAction = userContinueAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String passcode = ivrRequest.getData();
        String mobileNumber = String.valueOf(session.getAttribute(IVR.Attributes.CALLER_ID));
        Patient patient = patients.findByMobileNumber(mobileNumber);

        if (patient == null)
            return userNotFoundAction.handle(ivrRequest, request, response);

        if (!patient.hasPasscode(passcode))
            return retryAction.handle(ivrRequest, request, response);

        session.invalidate();
        session = request.getSession();
        session.setAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID, patient.getId());
        session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.AUTH_SUCCESS);
        return userContinueAction.handle(ivrRequest, request, response);
    }
}
