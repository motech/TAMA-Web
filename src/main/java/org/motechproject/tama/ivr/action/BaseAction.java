package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAction implements IVRAction {
    protected IVRMessage messages;
    protected IVRCallAudits audits;

    protected String responseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withPlayText(playText).create();
        return ivrResponse.getXML();
    }

    protected String hangUpResponseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withPlayText(playText).withHangUp().create();
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayText(playText).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWithWav(IVRRequest ivrRequest, String key) {
        String playAudio = messages.get(key);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayAudio(playAudio).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }
}
