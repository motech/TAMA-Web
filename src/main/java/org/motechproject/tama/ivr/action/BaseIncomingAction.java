package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseIncomingAction implements IVRIncomingAction {
    @Autowired
    protected IVRMessage messages;
    protected IVRCallAudits audits;

    protected String hangUpResponseWith(IVRRequest ivrRequest) {
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withHangUp().create();
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWithWav(IVRRequest ivrRequest, String key) {
        String playAudio = messages.get(key);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayAudio(playAudio).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }

    protected void audit(IVRRequest ivrRequest, String patientId, IVRCallAudit.State state) {
        audits.add(new IVRCallAudit(ivrRequest.getCid(), ivrRequest.getSid(), patientId, state));
    }
}
