package org.motechproject.tama.ivr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.pillreminder.IvrAction;
import org.motechproject.tama.ivr.decisiontree.TreeChooser;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateAction extends BaseAction {
	private PillReminderService pillReminderService;
    private RetryAction retryAction;

    private AllPatients allPatients;
    private ThreadLocalTargetSource threadLocalTargetSource;
    private final TreeChooser treeChooser;
    private UserNotFoundAction userNotFoundAction;

    @Autowired
    public AuthenticateAction(PillReminderService pillReminderService, AllPatients allPatients, RetryAction retryAction, ThreadLocalTargetSource threadLocalTargetSource, TreeChooser treeChooser, UserNotFoundAction userNotFoundAction) {
        this.pillReminderService = pillReminderService;
        this.allPatients = allPatients;
        this.retryAction = retryAction;
        this.threadLocalTargetSource = threadLocalTargetSource;
        this.treeChooser = treeChooser;
        this.userNotFoundAction = userNotFoundAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return handle(ivrRequest, request, response, new IvrAction(treeChooser, messages, threadLocalTargetSource));
    }

    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response, IvrAction tamaIvrAction) {
        IVRSession ivrSession = getIVRSession(request);
        String passcode = ivrRequest.getInput();
        Patient patient = allPatients.findByMobileNumber(ivrRequest.getCid());

        if(patient == null) return userNotFoundAction.handle(ivrRequest, request, response);
        patient = allPatients.findByMobileNumberAndPasscode(ivrRequest.getCid(), passcode);
        if (!isAuthenticatedUser(passcode, patient)) {
            return retryAction.handle(ivrRequest, request, response);
        }
        if (!patient.isActive()) return userNotFoundAction.handle(ivrRequest, request, response);

        ivrSession.renew(request);
        ivrSession.setState(IVRCallState.AUTH_SUCCESS);
        ivrSession.set(IVRCallAttribute.PATIENT_DOC_ID, patient.getId());
        ivrSession.set(IVRCallAttribute.PREFERRED_LANGUAGE_CODE, patient.getIvrLanguage().getCode());

        ivrSession.set(IVRCallAttribute.CALL_TIME, DateUtil.now());
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patient.getId());
        ivrSession.set(IVRCallAttribute.REGIMEN_FOR_PATIENT, pillRegimen);
        ivrSession.set(IVRCallAttribute.SYMPTOMS_REPORTING_PARAM, request.getParameter(IVRCallAttribute.SYMPTOMS_REPORTING_PARAM));
        ivrRequest.setData("");
        return tamaIvrAction.handle(ivrRequest, ivrSession);
    }

    private boolean isAuthenticatedUser(String passcode, Patient patient) {
        return patient != null && patient.authenticatedWith(passcode);
    }

}
