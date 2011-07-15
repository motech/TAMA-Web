package org.motechproject.tama.ivr.action.event;

import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class NewCallEventAction extends BaseAction {

    public NewCallEventAction() {
    }

    public NewCallEventAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
        session.setAttribute(IVR.Attributes.CALL_ID, ivrRequest.getSid());
        session.setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
        return dtmfResponseWith(ivrRequest, IVR.MessageKey.TAMA_IVR_ASK_FOR_PIN);
    }
}
