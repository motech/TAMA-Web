package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.tama.ivr.action.IVRAction;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class HangupEventAction extends BaseAction {
    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
