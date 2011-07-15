package org.motechproject.tama.ivr.action;

import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserNotAuthorisedAction extends BaseAction {

    public UserNotAuthorisedAction() {
    }

    public UserNotAuthorisedAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return hangUpResponseWith(ivrRequest, IVR.MessageKey.TAMA_IVR_REPORT_USER_NOT_AUTHORISED);
    }
}
