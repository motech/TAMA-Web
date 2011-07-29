package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
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
public class PreviousDosageMenuAction extends BaseIncomingAction {
    private ActionMenu menu = new ActionMenu();

    @Autowired
    public PreviousDosageMenuAction(IVRMessage messages, IVRCallAudits audits, PreviousDosageTakenAction previousDoseTakenAction, PreviousDosageNotTakenAction previousDoseNotTakenAction) {
        this.audits = audits;
        this.messages = messages;
        this.menu.add(previousDoseTakenAction, previousDoseNotTakenAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRIncomingAction action = menu.get(getInput(ivrRequest));
        if (action != null)
            return action.handle(ivrRequest, request, response);

        IVRResponseBuilder builder = new IVRResponseBuilder().withSid(ivrRequest.getSid());
        builder.withCollectDtmf(new IVRDtmfBuilder().withPlayAudio(messages.getWav(IVRMessage.PREVIOUS_DOSE_MENU)).create());
        return builder.create().getXML();
    }
}
