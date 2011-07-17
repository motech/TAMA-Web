package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
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
        if (ivrRequest.hasNoData())
            return dtmfResponseWith(ivrRequest, IVR.MessageKey.TAMA_IVR_REMIND_FOR_PIN);

        session.setAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS, ++attempt);

        String playText = messages.get(IVR.MessageKey.TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE);
        String playAudio = messages.get(IVR.MessageKey.TAMA_SIGNATURE_MUSIC_URL);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayText(playText).withPlayAudio(playAudio).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }

    private Integer getAttempt(HttpSession session) {
        Object attempts = session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS);
        return attempts == null ? 0 : (Integer) attempts;
    }

    private boolean isLast(Integer attempts) {
        return maxNoOfAttempts.equals(attempts + 1);
    }
}
