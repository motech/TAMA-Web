package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.repository.AllIVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserNotAuthorisedAction extends BaseAction {
    @Autowired
    public UserNotAuthorisedAction(IVRMessage messages, AllIVRCallAudits audits) {
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        String id = ivrSession.getPatientId();
        audit(ivrRequest, id, IVRCallAudit.State.PASSCODE_ENTRY_FAILED);
        ivrSession.renew(request);
        return hangUpResponseWith(ivrRequest);
    }
}
