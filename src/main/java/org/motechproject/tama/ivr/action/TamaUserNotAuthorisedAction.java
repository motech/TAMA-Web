package org.motechproject.tama.ivr.action;

import org.motechproject.ivr.kookoo.action.BaseAction;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.audit.AuditService;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class TamaUserNotAuthorisedAction extends BaseAction {

    private AuditService auditService;

    @Autowired
    public TamaUserNotAuthorisedAction(IVRMessage messages, AuditService auditService) {
        this.auditService = auditService;
        this.messages = messages;
    }

    @Override
    public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        String id = TamaSessionUtil.getPatientId(ivrSession);
        auditService.audit(ivrRequest, id, IVRCallAudit.State.PASSCODE_ENTRY_FAILED);
        ivrSession.renew(request);
        return hangUpResponseWith(ivrRequest);
    }
}
