package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseIncomingAction implements IVRIncomingAction {
    @Autowired
    protected IVRMessage messages;
    protected IVRCallAudits audits;

    public static final String POUND_SYMBOL = "%23";

    protected String hangUpResponseWith(IVRRequest ivrRequest) {
        Response ivrResponse = new IVRResponseBuilder(ivrRequest.getSid()).withHangUp().create(messages);
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWithWav(IVRRequest ivrRequest, String key) {
        Response ivrResponse = new IVRResponseBuilder(ivrRequest.getSid()).collectDtmf().withPlayAudios(key).create(messages);
        return ivrResponse.getXML();
    }

    protected void audit(IVRRequest ivrRequest, String patientId, IVRCallAudit.State state) {
        audits.add(new IVRCallAudit(ivrRequest.getCid(), ivrRequest.getSid(), patientId, state));
    }

    protected IVRSession getIVRSession(HttpServletRequest request) {
        return new IVRSession(request.getSession(false));
    }

    protected IVRSession createIVRSession(HttpServletRequest request) {
        return new IVRSession(request.getSession());
    }

    protected String getInput(IVRRequest ivrRequest) {
        return StringUtils.remove(ivrRequest.getData(), POUND_SYMBOL);
    }

    @Override
    public String getKey() {
        return StringUtils.EMPTY;
    }
}
