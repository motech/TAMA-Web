package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.pillreminder.IVRAction;
import org.motechproject.tama.ivr.decisiontree.CurrentDosageReminderTree;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthenticateAction extends BaseIncomingAction {
    private Patients patients;
    private RetryAction retryAction;
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Autowired
    public AuthenticateAction(Patients patients, RetryAction retryAction, CurrentDosageReminderTree currentDosageReminderTree) {
        this.patients = patients;
        this.retryAction = retryAction;
        this.currentDosageReminderTree = currentDosageReminderTree;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return handle(ivrRequest, request, response, new IVRAction(currentDosageReminderTree, messages));
    }

    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response, IVRAction tamaIvrAction) {
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
        ivrRequest.setData("");
        return tamaIvrAction.handle(ivrRequest, ivrSession);
    }
}
