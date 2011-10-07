package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
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

@Controller
@RequestMapping("/postoutbox")
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
        IVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();

        String redirectedAfterTree = (String) session.getAttribute(TamaSessionUtil.TamaSessionAttribute.POST_TREE_CALL_CONTINUE);
        if ("true".equals(redirectedAfterTree)) {
            ivrResponseBuilder.withPlayAudios(TamaIVRMessage.THOSE_WERE_YOUR_MESSAGES);
        } else {
            String lastPlayedMessageInSession = (String) session.getAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID);
            if (lastPlayedMessageInSession == null)
                ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NO_MESSAGES);

            ivrResponseBuilder.withPlayAudios(TamaIVRMessage.HANGUP_OR_MAIN_MENU);
            ivrResponseBuilder.withHangUp();
        }
        ivrResponseBuilder.withPlayAudios(tamaIvrMessage.getSignatureMusic());
        String preferredLanguage = (String) session.getAttribute(IVRSession.IVRCallAttribute.PREFERRED_LANGUAGE_CODE);
        return ivrResponseBuilder.create(tamaIvrMessage, null, preferredLanguage);
    }
}