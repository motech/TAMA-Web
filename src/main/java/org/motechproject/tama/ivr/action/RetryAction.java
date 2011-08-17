package org.motechproject.tama.ivr.action;

import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class RetryAction extends BaseIncomingAction {
    @Value("#{ivrProperties['max.number.of.attempts']}")
    private Integer maxNoOfAttempts;
    @Autowired
    private UserNotAuthorisedAction userNotAuthorisedAction;

    public RetryAction() {
    }

    public RetryAction(UserNotAuthorisedAction userNotAuthorisedAction, Integer maxNoOfAttempts, IVRMessage messages) {
        this.userNotAuthorisedAction = userNotAuthorisedAction;
        this.maxNoOfAttempts = maxNoOfAttempts;
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        Integer attempt = getAttempt(ivrSession);
        if (isLast(attempt))
            return userNotAuthorisedAction.handle(ivrRequest, request, response);

        if (!ivrRequest.hasNoData())
           ivrSession.set(IVRCallAttribute.NUMBER_OF_ATTEMPTS, ++attempt);
        
        return dtmfResponseWithWav(ivrRequest, ivrSession, IVRMessage.SIGNATURE_MUSIC_URL);
    }

    private Integer getAttempt(IVRSession ivrSession) {
        Integer attempts = ivrSession.getInt(IVRCallAttribute.NUMBER_OF_ATTEMPTS);
        return attempts == null ? 0 : attempts;
    }

    private boolean isLast(Integer attempts) {
        return maxNoOfAttempts.equals(attempts + 1);
    }
}
