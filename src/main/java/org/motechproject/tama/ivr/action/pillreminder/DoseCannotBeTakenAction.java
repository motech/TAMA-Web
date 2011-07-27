package org.motechproject.tama.ivr.action.pillreminder;

import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.ActionMenu;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.IVRIncomingAction;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class DoseCannotBeTakenAction extends BaseIncomingAction {

    public static final String KEY = "3";

    private ActionMenu menu = new ActionMenu();

    private DoNotHavePillsAction doNotHavePillsAction;

    @Autowired
    public DoseCannotBeTakenAction(IVRMessage messages, DoNotHavePillsAction doNotHavePillsAction) {
        this.messages = messages;
        this.menu.add(doNotHavePillsAction);
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String ivrData = getIVRData(ivrRequest);
        IVRIncomingAction action = menu.get(ivrData);
        if (action != null)
            return action.handle(ivrRequest, request, response);

        IVRResponseBuilder builder = new IVRResponseBuilder().withSid(ivrRequest.getSid());
        builder.withCollectDtmf(new IVRDtmfBuilder().withPlayAudio(messages.getWav(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU)).create());
        return builder.create().getXML();
    }

    @Override
    public String getKey() {
        return KEY;
    }
}
