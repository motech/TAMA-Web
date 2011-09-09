package org.motechproject.tama.ivr.action.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRRequest;
import org.springframework.stereotype.Service;

@Service
public class HangupEventAction extends BaseEventAction {
    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        getIVRSession(request).close();
        return;
    }
    @Override
    public IVREvent getCallEventName() {
    	return IVREvent.HANGUP;
    }
}
