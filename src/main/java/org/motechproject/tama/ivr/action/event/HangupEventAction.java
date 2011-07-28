package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class HangupEventAction extends BaseIncomingAction {
    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        getIVRSession(request).close();
        return null;
    }
}
