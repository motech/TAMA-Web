package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.ActionMenu;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.IVRIncomingAction;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DoseCannotBeTakenAction extends BaseIncomingAction {
    public static final String KEY = "3";
    private ActionMenu menu = new ActionMenu();

    @Autowired
    public DoseCannotBeTakenAction(IVRMessage messages, DoNotHavePillsAction doNotHavePillsAction) {
        this.messages = messages;
        this.menu.add(doNotHavePillsAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRSession ivrSession = getIVRSession(request);
        ivrSession.setState(IVRCallState.COLLECT_DOSE_CANNOT_BE_TAKEN);

        IVRIncomingAction action = menu.get(getInput(ivrRequest));
        if (action != null)
            return action.handle(ivrRequest, request, response);

        IVRResponseBuilder builder = new IVRResponseBuilder(ivrRequest.getSid());
        builder.collectDtmf().withPlayAudios(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU);
        return builder.create(messages).getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
