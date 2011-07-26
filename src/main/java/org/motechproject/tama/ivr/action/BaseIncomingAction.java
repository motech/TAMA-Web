package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
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
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withHangUp().create();
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWithWav(IVRRequest ivrRequest, String key) {
        String playAudio = messages.getWav(key);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayAudio(playAudio).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
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

    protected String getIVRData(IVRRequest ivrRequest) {
        return StringUtils.remove(ivrRequest.getData(), POUND_SYMBOL);
    }
}
