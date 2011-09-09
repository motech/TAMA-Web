package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
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
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.util.DateUtil;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthenticateAction extends BaseAction {
    private PillReminderService pillReminderService;
    private RetryAction retryAction;
    private UserNotFoundAction userNotFoundAction;
    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private final TreeChooser treeChooser;
    private ThreadLocalTargetSource threadLocalTargetSource;

    @Autowired
    public AuthenticateAction(PillReminderService pillReminderService,
                              AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices,
                              RetryAction retryAction, UserNotFoundAction userNotFoundAction,
                              ThreadLocalTargetSource threadLocalTargetSource, TreeChooser treeChooser) {
        this.pillReminderService = pillReminderService;
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
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
        String phoneNumber = (String) ivrSession.get(IVRCallAttribute.CALLER_ID);
        Patient patient = allPatients.findByMobileNumber(phoneNumber);

        if(patient == null)
            return userNotFoundAction.handle(ivrRequest, request, response);

        patient = allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode);

        if (!isAuthenticatedUser(passcode, patient)) {
            return retryAction.handle(ivrRequest, request, response);
        }
        if (!patient.isActive() || (!isSymptomReportingCall(request) && hasNoTreatmentAdvice(patient)))
            return userNotFoundAction.handle(ivrRequest, request, response);

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

    private boolean hasNoTreatmentAdvice(Patient patient) {
        return allTreatmentAdvices.findByPatientId(patient.getId()) == null;
    }

    private boolean isSymptomReportingCall(HttpServletRequest request) {
        return StringUtils.isNotBlank(request.getParameter(IVRCallAttribute.SYMPTOMS_REPORTING_PARAM));
    }

}
