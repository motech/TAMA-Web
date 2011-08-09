package org.motechproject.tama.ivr.action;

import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.pillreminder.IVRAction;
import org.motechproject.tama.ivr.decisiontree.CurrentDosageReminderTree;
import org.motechproject.tama.repository.Patients;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthenticateAction extends BaseIncomingAction {
    private PillReminderService pillReminderService;
    private Patients patients;
    private RetryAction retryAction;
    private CurrentDosageReminderTree currentDosageReminderTree;
    private ThreadLocalTargetSource threadLocalTargetSource;

    @Autowired
    public AuthenticateAction(PillReminderService pillReminderService, Patients patients, RetryAction retryAction, CurrentDosageReminderTree currentDosageReminderTree, ThreadLocalTargetSource threadLocalTargetSource) {
        this.pillReminderService = pillReminderService;
        this.patients = patients;
        this.retryAction = retryAction;
        this.currentDosageReminderTree = currentDosageReminderTree;
        this.threadLocalTargetSource = threadLocalTargetSource;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return handle(ivrRequest, request, response, new IVRAction(currentDosageReminderTree, messages, threadLocalTargetSource));
    }

    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response, IVRAction tamaIvrAction) {
        IVRSession ivrSession = getIVRSession(request);
        String passcode = ivrRequest.getInput();
        String id = ivrSession.getPatientId();
        Patient patient = patients.get(id);

        if (!patient.authenticatedWith(passcode)) {
            return retryAction.handle(ivrRequest, request, response);
        }
        String patientId = ivrSession.getPatientId();
        ivrSession.renew(request);
        ivrSession.setState(IVRCallState.AUTH_SUCCESS);
        ivrSession.set(IVRCallAttribute.PATIENT_DOC_ID, patientId);
        ivrSession.set(IVRCallAttribute.CALL_TIME, DateUtil.now());
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patient.getId());
        ivrSession.set(IVRCallAttribute.REGIMEN_FOR_PATIENT, pillRegimen);
        ivrRequest.setData("");
        return tamaIvrAction.handle(ivrRequest, ivrSession);
    }
}
