package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.ActionMenu;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.IVRIncomingAction;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class CurrentDosageMenuAction extends BaseIncomingAction {
    private ActionMenu menu = new ActionMenu();

    @Autowired
    public CurrentDosageMenuAction(IVRMessage messages, IVRCallAudits audits,
                                   DoseCannotBeTakenAction doseNotTakenAction, DoseTakenAction doseTakenAction,
                                   DoseWillBeTakenAction doseWillBeTakenAction, DoseRemindAction doseRemindAction) {
        this.audits = audits;
        this.messages = messages;
        this.menu.add(doseRemindAction, doseNotTakenAction, doseTakenAction, doseWillBeTakenAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);

        if (ivrSession.isDoseCannotBeTaken())
            return menu.get(DoseCannotBeTakenAction.KEY).handle(ivrRequest, request, response);

        if (!ivrSession.isDoseResponse())
            return menu.get(DoseRemindAction.KEY).handle(ivrRequest, request, response);

        IVRIncomingAction action = menu.get(getInput(ivrRequest));
        if (action != null)
            return action.handle(ivrRequest, request, response);

        IVRResponseBuilder builder = new IVRResponseBuilder().withSid(ivrRequest.getSid());
        builder.withCollectDtmf(new IVRDtmfBuilder().withPlayAudio(messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU)).create());
        return builder.create().getXML();
    }
}
