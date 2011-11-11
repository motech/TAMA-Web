package org.motechproject.tama.platform.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.IVRAuthenticationStatus;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllIVRCallAudits;
import org.motechproject.tama.repository.AllPatients;
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

    public IVRAuthenticationStatus checkAccess(String phoneNumber, String passcode, int attemptNumber, String sid) {
        Patient likelyPatient = allPatients.findByMobileNumber(phoneNumber);
        if (likelyPatient == null) {
            ivrCallAudits.add(new IVRCallAudit(phoneNumber, sid, "", IVRCallAudit.State.PASSCODE_ENTRY_FAILED));
            return IVRAuthenticationStatus.notFound();
        }

        Patient patient = allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode);
        if (patient == null) {
            ivrCallAudits.add(new IVRCallAudit(phoneNumber, sid, likelyPatient.getId(), IVRCallAudit.State.PASSCODE_ENTRY_FAILED));
            IVRAuthenticationStatus ivrAuthenticationStatus = IVRAuthenticationStatus.notAuthenticated();
            ivrAuthenticationStatus.allowRetry(StringUtils.isEmpty(passcode) || !maxNoOfAttempts.equals(attemptNumber));
            ivrAuthenticationStatus.loginAttemptNumber(StringUtils.isEmpty(passcode) ? --attemptNumber : attemptNumber);
            return ivrAuthenticationStatus;
        }

        IVRAuthenticationStatus status = IVRAuthenticationStatus.authenticated(patient.getId());
        return status.active(patient.isActive() || patient.isSuspended()).language(patient.getPatientPreferences().getIvrLanguage().getCode());
    }
}
