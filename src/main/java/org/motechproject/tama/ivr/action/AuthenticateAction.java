package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.pillreminder.DosageMenuAction;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthenticateAction extends BaseIncomingAction {
    private Patients patients;
    private RetryAction retryAction;
    private UserNotFoundAction userNotFoundAction;
    private DosageMenuAction pillReminderAction;

    @Autowired
    public AuthenticateAction(Patients patients, RetryAction retryAction, UserNotFoundAction userNotFoundAction, DosageMenuAction pillReminderAction) {
        this.patients = patients;
        this.retryAction = retryAction;
        this.userNotFoundAction = userNotFoundAction;
        this.pillReminderAction = pillReminderAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        String passcode = getInput(ivrRequest);
        String id = ivrSession.getPatientId();
        Patient patient = patients.get(id);

        if (!patient.authenticatedWith(passcode)) {
            return retryAction.handle(ivrRequest, request, response);
        }
        String patientId = ivrSession.get(IVRCallAttribute.PATIENT_DOC_ID);
        ivrSession.renew(request);
        ivrSession.setState(IVRCallState.AUTH_SUCCESS);
        ivrSession.set(IVRCallAttribute.PATIENT_DOC_ID, patientId);
        return pillReminderAction.handle(ivrRequest, request, response);
    }

}
