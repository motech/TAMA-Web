package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.PillReminderAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class DtmfEventAction extends BaseIncomingAction {
    private AuthenticateAction authenticateAction;
    private PillReminderAction pillReminderAction;

    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction, PillReminderAction pillReminderAction) {
        this.authenticateAction = authenticateAction;
        this.pillReminderAction = pillReminderAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        IVR.CallState callState = (IVR.CallState) session.getAttribute(IVR.Attributes.CALL_STATE);
        return callState.isCollectPin() ?
                authenticateAction.handle(ivrRequest, request, response) :
                pillReminderAction.handle(ivrRequest, request, response);
    }

}
