package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.pillreminder.PillReminderMenuAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DtmfEventAction extends BaseIncomingAction {
    private AuthenticateAction authenticateAction;
    private PillReminderMenuAction pillReminderAction;

    @Autowired
    public DtmfEventAction(AuthenticateAction authenticateAction, PillReminderMenuAction pillReminderAction) {
        this.authenticateAction = authenticateAction;
        this.pillReminderAction = pillReminderAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        return ivrSession.isAuthentication()?
                authenticateAction.handle(ivrRequest, request, response) :
                pillReminderAction.handle(ivrRequest, request, response);
    }

}
