package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseAction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DisconnectEventAction extends BaseEventAction {
    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request,
    		HttpServletResponse response) {
        getIVRSession(request).close();
    }
}
