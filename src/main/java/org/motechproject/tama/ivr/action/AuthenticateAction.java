package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class AuthenticateAction extends BaseIncomingAction {
    public static final String POUND_SYMBOL = "%23";
    private Patients patients;
    private RetryAction retryAction;
    private UserNotFoundAction userNotFoundAction;
    private PillReminderAction userContinueAction;

    @Autowired
    public AuthenticateAction(Patients patients, RetryAction retryAction, UserNotFoundAction userNotFoundAction, PillReminderAction userContinueAction) {
        this.patients = patients;
        this.retryAction = retryAction;
        this.userNotFoundAction = userNotFoundAction;
        this.userContinueAction = userContinueAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String passcode = StringUtils.remove(ivrRequest.getData(), POUND_SYMBOL);

        Patient patient = patients.get((String) session.getAttribute(IVR.Attributes.PATIENT_DOC_ID));
        if (!patient.authenticateForIVRWith(passcode)) {
            return retryAction.handle(ivrRequest, request, response);
        }
        session = recreateSession(request, session);
        session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.AUTH_SUCCESS);
        return userContinueAction.handle(ivrRequest, request, response);
    }

    private HttpSession recreateSession(HttpServletRequest request, HttpSession session) {
        String patientId = (String) session.getAttribute(IVR.Attributes.PATIENT_DOC_ID);
        session.invalidate();
        session = request.getSession();
        session.setAttribute(IVR.Attributes.PATIENT_DOC_ID, patientId);
        return session;
    }
}
