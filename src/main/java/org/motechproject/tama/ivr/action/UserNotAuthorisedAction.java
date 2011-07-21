package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class UserNotAuthorisedAction extends BaseAction {

    @Autowired
    public UserNotAuthorisedAction(IVRMessage messages, IVRCallAudits audits) {
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String id = (String) session.getAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID);
        audits.add(new IVRCallAudit(ivrRequest.getCid(), ivrRequest.getSid(), id, IVRCallAudit.State.PASSCODE_ENTRY_FAILED));
        session.invalidate();
        return hangUpResponseWith(ivrRequest);
    }
}
