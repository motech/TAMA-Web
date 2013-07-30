package org.motechproject.tama.ivr.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.IVRAuthenticationStatus;
import org.motechproject.tama.ivr.domain.IVRCallAudit;
import org.motechproject.tama.ivr.repository.AllIVRCallAudits;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    private AllPatients allPatients;
    private AllIVRCallAudits ivrCallAudits;

    @Value("#{ivrProperties['max.number.of.attempts']}")
    Integer maxNoOfAttempts;

    @Autowired
    public AuthenticationService(AllPatients allPatients, AllIVRCallAudits ivrCallAudits) {
        this.allPatients = allPatients;
        this.ivrCallAudits = ivrCallAudits;
    }

    public boolean allowAccess(String callerId, String sid) {
        boolean doAllowAccess = callerId != null && (allPatients.findByMobileNumber(callerId) != null);
        if (!doAllowAccess)
            ivrCallAudits.add(new IVRCallAudit(callerId, sid, StringUtils.EMPTY, IVRCallAudit.State.USER_NOT_FOUND));
        return doAllowAccess;
    }

    public IVRAuthenticationStatus checkAccess(TAMAIVRContext context) {
        if (context.isOutgoingCall()) {
            return checkAccessOutGoingCall(context.getKooKooIVRContext().externalId(), context.callerId(), context.dtmfInput(), context.numberOfLoginAttempts() + 1, context.callId(), context.isOutgoingCall());
        } else {
            return checkAccess(context.callerId(), context.dtmfInput(), context.numberOfLoginAttempts() + 1, context.callId(), context.isOutgoingCall());
        }
    }

    protected IVRAuthenticationStatus checkAccess(String phoneNumber, String passCode, int attemptNumber, String sid, boolean isOutgoingCall) {
        Patient likelyPatient = allPatients.findByMobileNumber(phoneNumber);
        if (likelyPatient == null) {
            ivrCallAudits.add(new IVRCallAudit(phoneNumber, sid, "", IVRCallAudit.State.PASSCODE_ENTRY_FAILED));
            return IVRAuthenticationStatus.notFound();
        }

        Patient patient = allPatients.findByMobileNumberAndPasscode(phoneNumber, passCode);
        return addIVRCallAuditAndAuthenticationStatus(likelyPatient.getPatientId(), patient, phoneNumber, passCode, attemptNumber, sid, isOutgoingCall);
    }

    protected IVRAuthenticationStatus checkAccessOutGoingCall(String patientID, String phoneNumber, String passCode, int attemptNumber, String sid, boolean isOutgoingCall) {
        Patient patient = allPatients.findByMobileNumberAndPasscodeAndPatientId(phoneNumber, passCode, patientID);
        return addIVRCallAuditAndAuthenticationStatus(patientID, patient, phoneNumber, passCode, attemptNumber, sid, isOutgoingCall);
    }

    private IVRAuthenticationStatus addIVRCallAuditAndAuthenticationStatus(String patientID, Patient patient, String phoneNumber, String passCode, int attemptNumber, String sid, boolean isOutgoingCall) {
        if (patient == null) {
            ivrCallAudits.add(new IVRCallAudit(phoneNumber, sid, patientID, IVRCallAudit.State.PASSCODE_ENTRY_FAILED));
            IVRAuthenticationStatus ivrAuthenticationStatus = IVRAuthenticationStatus.notAuthenticated();
            ivrAuthenticationStatus.allowRetry(StringUtils.isEmpty(passCode) || !maxNoOfAttempts.equals(attemptNumber));
            ivrAuthenticationStatus.loginAttemptNumber(StringUtils.isEmpty(passCode) ? --attemptNumber : attemptNumber);
            return ivrAuthenticationStatus;
        }

        IVRAuthenticationStatus status = IVRAuthenticationStatus.authenticated(patient.getId());
        return status.allowCall(isOutgoingCall || patient.allowIncomingCalls()).language(patient.getPatientPreferences().getIvrLanguage().getCode());
    }

}
