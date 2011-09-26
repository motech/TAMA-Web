package org.motechproject.tama.ivr.action;

import org.motechproject.ivr.kookoo.action.BaseAction;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class TamaRetryAction extends BaseAction {
    @Value("#{ivrProperties['max.number.of.attempts']}")
    private Integer maxNoOfAttempts;
    @Autowired
    private TamaUserNotAuthorisedAction userNotAuthorisedAction;

    public TamaRetryAction() {
    }

    public TamaRetryAction(TamaUserNotAuthorisedAction userNotAuthorisedAction, Integer maxNoOfAttempts, IVRMessage messages) {
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

        ivrSession.set(IVRCallAttribute.NUMBER_OF_ATTEMPTS, ++attempt);
        
        return dtmfResponseWithWav(ivrRequest, messages.getSignatureMusic());
    }

    private Integer getAttempt(IVRSession ivrSession) {
        Integer attempts = ivrSession.getInt(IVRCallAttribute.NUMBER_OF_ATTEMPTS);
        return attempts == null ? 0 : attempts;
    }

    private boolean isLast(Integer attempts) {
        return maxNoOfAttempts.equals(attempts + 1);
    }
}
