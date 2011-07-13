package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class NewCallAction implements IVRAction {
    @Autowired
    private Patients patients;
    @Autowired
    private IVRMessage messages;

    public NewCallAction() {
    }

    public NewCallAction(Patients patients, IVRMessage messages) {
        this.patients = patients;
        this.messages = messages;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
        session.setAttribute(IVR.Attributes.CALL_ID, ivrRequest.getSid());
        session.setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());

        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayText(messages.get(IVRMessage.Key.TAMA_IVR_ASK_FOR_PIN)).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }
}
