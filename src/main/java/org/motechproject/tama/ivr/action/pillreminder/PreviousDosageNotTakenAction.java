package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class PreviousDosageNotTakenAction extends BaseIncomingAction {

    public static final String KEY = "3";

    @Autowired
    public PreviousDosageNotTakenAction(IVRMessage messages, IVRCallAudits audits) {
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        Response ivrResponse = new IVRResponseBuilder()
                .withSid(ivrRequest.getSid())
                .addPlayAudio(
                        messages.getWav(IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE),
                        messages.getWav(IVRMessage.YESTERDAYS),
                        messages.getWav(IVRMessage.EVENING),
                        messages.getWav(IVRMessage.DOSE),
                        messages.getWav(IVRMessage.TRY_NOT_TO_MISS)
                )
                .withHangUp()
                .create();
        return ivrResponse.getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
