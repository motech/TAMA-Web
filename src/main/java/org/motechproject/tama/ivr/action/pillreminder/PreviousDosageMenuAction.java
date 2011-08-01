package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.IVRCallAudits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Service
public class PreviousDosageMenuAction extends BaseIncomingAction {
    private HashMap<String, BaseIncomingAction> menu = new HashMap<String, BaseIncomingAction>();

    @Autowired
    public PreviousDosageMenuAction(IVRMessage messages, IVRCallAudits audits, PreviousDosageTakenAction previousDoseTakenAction, PreviousDosageNotTakenAction previousDoseNotTakenAction) {
        this.audits = audits;
        this.messages = messages;
        this.menu.put("1", previousDoseTakenAction);
        this.menu.put("3", previousDoseNotTakenAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        BaseIncomingAction action = menu.get(getInput(ivrRequest));
        if (action != null)
            return action.handle(ivrRequest, request, response);

        IVRResponseBuilder builder = new IVRResponseBuilder(ivrRequest.getSid());
        return builder.collectDtmf().withPlayAudios(IVRMessage.PREVIOUS_DOSE_MENU).create(messages).getXML();
    }
}
