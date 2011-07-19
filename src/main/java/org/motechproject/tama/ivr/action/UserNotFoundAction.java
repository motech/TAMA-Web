package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class UserNotFoundAction extends BaseAction {

    @Autowired
    public UserNotFoundAction(IVRMessage messages, IVRCallAudits audits) {
        this.audits = audits;
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        audits.add(new IVRCallAudit(ivrRequest.getCid(), ivrRequest.getSid(), StringUtils.EMPTY, IVRCallAudit.State.USER_NOT_FOUND));
        return hangUpResponseWith(ivrRequest, IVRMessage.TAMA_IVR_REPORT_USER_NOT_FOUND);
    }
}
