package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.IVRIncomingAction;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class PillReminderMenuAction extends BaseIncomingAction {
    private Patients patients;
    private Clinics clinics;
    private PillReminderService service;
    private Map<String, IVRIncomingAction> menuActions = new HashMap<String, IVRIncomingAction>();

    @Autowired
    public PillReminderMenuAction(IVRMessage messages, Patients patients, Clinics clinics, IVRCallAudits audits, PillReminderService service,
                                  DoseCannotBeTakenAction doseNotTakenAction, DoseTakenAction doseTakenAction,
                                  DoseWillBeTakenAction doseWillBeTakenAction, DoseRemindAction doseRemindAction) {
        this.patients = patients;
        this.clinics = clinics;
        this.service = service;
        this.audits = audits;
        this.messages = messages;
        this.menuActions.put(DoseRemindAction.KEY, doseRemindAction);
        this.menuActions.put(DoseTakenAction.KEY, doseTakenAction);
        this.menuActions.put(DoseWillBeTakenAction.KEY, doseWillBeTakenAction);
        this.menuActions.put(DoseCannotBeTakenAction.KEY, doseNotTakenAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (!ivrSession.isDoseResponse())
            return menuActions.get(DoseRemindAction.KEY).handle(ivrRequest, request, response);

        IVRIncomingAction action = menuActions.get(getIVRData(ivrRequest));
        if (action != null)
            return action.handle(ivrRequest, request, response);

        IVRResponseBuilder builder = new IVRResponseBuilder().withSid(ivrRequest.getSid());
        builder.withCollectDtmf(new IVRDtmfBuilder().withPlayAudio(messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU)).create());
        return builder.create().getXML();
    }
}
