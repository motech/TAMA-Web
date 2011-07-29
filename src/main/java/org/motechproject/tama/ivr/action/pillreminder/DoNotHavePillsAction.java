package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DoNotHavePillsAction extends BaseIncomingAction {

    private static final String KEY = "2";
    private PillReminderService service;

    @Autowired
    public DoNotHavePillsAction(PillReminderService service, IVRMessage messages) {
        this.service = service;
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        ivrSession.setState(IVRCallState.COLLECT_PREVIOUS_DOSE_RESPONSE);

        Response ivrResponse = new IVRResponseBuilder()
                .withSid(ivrRequest.getSid())
                .addPlayAudio(messages.getWav(IVRMessage.PLEASE_CARRY_SMALL_BOX))
                .withPreviousDosageReminder(ivrRequest, service, messages)
                .create();
        return ivrResponse.getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
