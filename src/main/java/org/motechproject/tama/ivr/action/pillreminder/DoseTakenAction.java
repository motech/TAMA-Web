package org.motechproject.tama.ivr.action.pillreminder;

import com.ozonetel.kookoo.Response;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
public class DoseTakenAction extends BaseIncomingAction {
    public static final String KEY = "1";

    private PillReminderService service;
    private IVRCallAudits audits;
    private IVRMessage messages;

    @Autowired
    public DoseTakenAction(PillReminderService service, IVRCallAudits audits, IVRMessage messages) {
        this.service = service;
        this.audits = audits;
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        Map tamaParams = ivrRequest.getTamaParams();
        String regimenId = (String) tamaParams.get(PillReminderCall.REGIMEN_ID);
        String dosageId = (String) tamaParams.get(PillReminderCall.DOSAGE_ID);
        service.updateDosageTaken(regimenId, dosageId);

        IVRSession ivrSession = getIVRSession(request);
        ivrSession.setState(IVRCallState.COLLECT_PREVIOUS_DOSE_RESPONSE);

        Response ivrResponse = new IVRResponseBuilder()
                .withSid(ivrRequest.getSid())
                .addPlayAudio(messages.getWav(IVRMessage.DOSE_TAKEN))
                .withPreviousDosageReminder(ivrRequest, service, messages)
                .create();
        return ivrResponse.getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
