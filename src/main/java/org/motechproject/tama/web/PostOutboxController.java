package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.motechproject.server.service.ivr.IVRRequest.CallDirection;

@Controller
@RequestMapping("/post_outbox")
public class PostOutboxController {

    private TamaIVRMessage tamaIvrMessage;

    @Autowired
    public PostOutboxController(TamaIVRMessage tamaIvrMessage) {
        this.tamaIvrMessage = tamaIvrMessage;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String play(HttpServletRequest request) {
        HttpSession session = request.getSession();
        KookooRequest.CallDirection callDirection = (IVRRequest.CallDirection) session.getAttribute(TamaSessionUtil.TamaSessionAttribute.CALL_DIRECTION);
        IVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        if(callDirection == CallDirection.Outbound){
            ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NO_MESSAGES_FOR_NOW);
        }else{
            ivrResponseBuilder.withPlayAudios(TamaIVRMessage.HANGUP_OR_MAIN_MENU);
            ivrResponseBuilder.withHangUp();
        }
        String preferredLanguage = (String) session.getAttribute(IVRSession.IVRCallAttribute.PREFERRED_LANGUAGE_CODE);
        return ivrResponseBuilder.create(tamaIvrMessage, null, preferredLanguage);
    }
}