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
public class DoseWillBeTakenAction extends BaseIncomingAction {
    public static final String KEY = "2";

    @Autowired
    public DoseWillBeTakenAction(IVRMessage messages, IVRCallAudits audits) {
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        Response ivrResponse = new IVRResponseBuilder()
                .withSid(ivrRequest.getSid())
                .addPlayAudio(
                        messages.getWav(IVRMessage.PLEASE_TAKE_DOSE),
                        messages.getWav(IVRMessage.PILL_REMINDER_RETRY_INTERVAL),
                        messages.getWav(IVRMessage.MINUTES))
                .withHangUp()
                .create();
        return ivrResponse.getXML();
    }
}
