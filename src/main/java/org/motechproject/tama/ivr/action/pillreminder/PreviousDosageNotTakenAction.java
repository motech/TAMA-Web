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
        Response ivrResponse = new IVRResponseBuilder(ivrRequest.getSid())
                .withPlayAudios(
                        IVRMessage.YOU_SAID_YOU_DID_NOT_TAKE,
                        IVRMessage.YESTERDAYS,
                        IVRMessage.EVENING,
                        IVRMessage.DOSE,
                        IVRMessage.TRY_NOT_TO_MISS
                )
                .withHangUp()
                .create(messages);
        return ivrResponse.getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
