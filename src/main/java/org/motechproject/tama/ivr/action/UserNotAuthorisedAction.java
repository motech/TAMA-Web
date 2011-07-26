package org.motechproject.tama.ivr.action;

import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class UserNotAuthorisedAction extends BaseIncomingAction {
    @Autowired
    public UserNotAuthorisedAction(IVRMessage messages, IVRCallAudits audits) {
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        String id = ivrSession.get(IVRCallAttribute.PATIENT_DOC_ID);
        audit(ivrRequest, id, IVRCallAudit.State.PASSCODE_ENTRY_FAILED);
        ivrSession.renew(request);
        return hangUpResponseWith(ivrRequest);
    }
}
