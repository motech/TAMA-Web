package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.IVRIncomingAction;
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
public class PillReminderAction extends BaseIncomingAction {
    private Patients patients;
    private Clinics clinics;
    private PillReminderService service;
    private Map<String, IVRIncomingAction> actions = new HashMap<String, IVRIncomingAction>();

    @Autowired
    public PillReminderAction(IVRMessage messages, Patients patients, Clinics clinics, IVRCallAudits audits, PillReminderService service,
                              DoseNotTakenAction doseNotTakenAction, DoseTakenAction doseTakenAction,
                              DoseWillBeTakenAction doseWillBeTakenAction, DoseRemindAction doseRemindAction) {
        this.patients = patients;
        this.clinics = clinics;
        this.service = service;
        this.audits = audits;
        this.messages = messages;
        this.actions.put(DoseRemindAction.KEY, doseRemindAction);
        this.actions.put(DoseTakenAction.KEY, doseTakenAction);
        this.actions.put(DoseWillBeTakenAction.KEY, doseWillBeTakenAction);
        this.actions.put(DoseNotTakenAction.KEY, doseNotTakenAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        if (ivrSession.isDoseResponse())
            return actions.get(getIVRData(ivrRequest)).handle(ivrRequest, request, response);
        return actions.get(DoseRemindAction.KEY).handle(ivrRequest, request, response);
    }

}
