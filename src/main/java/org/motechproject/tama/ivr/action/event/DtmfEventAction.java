package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.AuthenticateAction;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.tama.ivr.action.UserContinueAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class DtmfEventAction extends BaseAction {
    @Autowired
    private AuthenticateAction authenticateAction;
    @Autowired
    private UserContinueAction userContinueAction;

    public DtmfEventAction() {
    }

    public DtmfEventAction(AuthenticateAction authenticateAction, UserContinueAction userContinueAction){
        this.authenticateAction = authenticateAction;
        this.userContinueAction = userContinueAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        IVR.CallState callState = (IVR.CallState) session.getAttribute(IVR.Attributes.CALL_STATE);
        return callState.isCollectPin() ?
                authenticateAction.handle(ivrRequest, request, response):
                userContinueAction.handle(ivrRequest, request, response);
    }


}
