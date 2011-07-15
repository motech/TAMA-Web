package org.motechproject.tama.ivr.action;

import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserNotFoundAction extends BaseAction {

    public UserNotFoundAction() {
    }

    public UserNotFoundAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return responseWith(ivrRequest, IVR.MessageKey.TAMA_IVR_REPORT_USER_NOT_FOUND);
    }
}
