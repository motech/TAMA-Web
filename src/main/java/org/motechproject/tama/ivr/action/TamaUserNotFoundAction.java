package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.action.UserNotFoundAction;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class TamaUserNotFoundAction extends UserNotFoundAction {

    private AuditService auditService;

    @Autowired
    public TamaUserNotFoundAction(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        auditService.audit(ivrRequest, StringUtils.EMPTY, IVRCallAudit.State.USER_NOT_FOUND);
        return hangUpResponseWith(ivrRequest);
    }
}
