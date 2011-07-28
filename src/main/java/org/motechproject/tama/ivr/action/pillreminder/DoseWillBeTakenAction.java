package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
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
    private PillReminderService service;

    @Autowired
    public DoseWillBeTakenAction(PillReminderService service, IVRMessage messages, IVRCallAudits audits) {
        this.service = service;
        this.messages = messages;
        this.audits = audits;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        ivrSession.setState(IVRCallState.COLLECT_PREVIOUS_DOSE_RESPONSE);

        Response ivrResponse = new IVRResponseBuilder(ivrRequest.getSid())
                .withPlayAudios(IVRMessage.PLEASE_TAKE_DOSE, IVRMessage.PILL_REMINDER_RETRY_INTERVAL, IVRMessage.MINUTES)
                .withPreviousDosageReminder(ivrRequest, service, messages)
                .withHangUp()
                .create(messages);
        return ivrResponse.getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }

}
