package org.motechproject.tama.ivr.action;

import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class RetryAction extends BaseAction {
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
        HttpSession session = request.getSession(false);
        Integer attempt = getAttempt(session);
        if (isLast(attempt)) {
            session.invalidate();
            return userNotAuthorisedAction.handle(ivrRequest, request, response);
        }
        session.setAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS, attempt + 1);
        return dtmfResponseWith(ivrRequest, IVR.MessageKey.TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE);
    }

    private Integer getAttempt(HttpSession session) {
        Object attempts = session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS);
        return attempts == null ? 0 : (Integer) attempts;
    }

    private boolean isLast(Integer attempts) {
        return maxNoOfAttempts.equals(attempts + 1);
    }
}
