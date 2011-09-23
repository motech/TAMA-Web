package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/outbox")
public class OutboxController {
    @Autowired
    private VoiceOutboxService outboxService;

    @Autowired
    private IVRMessage ivrMessage;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String play(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String patientId = (String) session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID);

        OutboundVoiceMessage outboundVoiceMessage = outboxService.getNextPendingMessage(patientId);
        outboxService.saveMessage(outboundVoiceMessage.getId());

        IVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();

        List<String> audioFiles = (List<String>) outboundVoiceMessage.getParameters().get("audioFiles");
        for (String audioFile : audioFiles) {
            ivrResponseBuilder.withPlayAudios(audioFile);
        }

        ivrResponseBuilder.withNextUrl(ivrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL));

        String preferredLanguage = (String) session.getAttribute(IVRSession.IVRCallAttribute.PREFERRED_LANGUAGE_CODE);
        return ivrResponseBuilder.create(ivrMessage, null, preferredLanguage);
    }
}