package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DoNotHavePillsAction extends BaseIncomingAction {

    private static final String KEY = "2";

    @Autowired
    public DoNotHavePillsAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        Response ivrResponse = new IVRResponseBuilder()
                .withSid(ivrRequest.getSid())
                .addPlayAudio(messages.getWav(IVRMessage.PLEASE_CARRY_SMALL_BOX))
                .withHangUp()
                .create();
        return ivrResponse.getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
