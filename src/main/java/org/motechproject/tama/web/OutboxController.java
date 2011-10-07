package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.OutboxCommand;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    public static final String AUDIO_FILES_KEY = "audioFiles";
    public static final String EVENT_REQUEST_PARAM = "event";
    static final String VOICE_MESSAGE_COMMAND_AUDIO = "command";

    private VoiceOutboxService outboxService;

    private TamaIVRMessage tamaIvrMessage;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    public OutboxController(VoiceOutboxService outboxService, TamaIVRMessage tamaIvrMessage) {
        this.outboxService = outboxService;
        this.tamaIvrMessage = tamaIvrMessage;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String play(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if ("hangup".equalsIgnoreCase(request.getParameter(EVENT_REQUEST_PARAM))) {
            session.invalidate();
            return null;
        }

        String patientId = (String) session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID);

        markPreviousMessageAsPlayed(session);

        OutboundVoiceMessage outboundVoiceMessage = outboxService.getNextPendingMessage(patientId);

        if (outboundVoiceMessage == null) {
            return postOutboxForwardResponse();
        } else {
            setLastPlayedMessage(session, outboundVoiceMessage);
            return voiceMessageResponse(session, outboundVoiceMessage);
        }
    }

    private String voiceMessageResponse(HttpSession session, OutboundVoiceMessage outboundVoiceMessage) {
        IVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        VoiceMessageType voiceMessageType = outboundVoiceMessage.getVoiceMessageType();

        if (voiceMessageType != null && "AudioCommand".equals(voiceMessageType.getVoiceMessageTypeName())) {
            addAudiosToBePlayedToResponse(session, outboundVoiceMessage, ivrResponseBuilder);
        }
        addAudioFilesToBePlayedToResponse(outboundVoiceMessage, ivrResponseBuilder);

        ivrResponseBuilder.withNextUrl(tamaIvrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL));
        String preferredLanguage = (String) session.getAttribute(IVRSession.IVRCallAttribute.PREFERRED_LANGUAGE_CODE);
        return ivrResponseBuilder.create(tamaIvrMessage, null, preferredLanguage);
    }

    private void addAudiosToBePlayedToResponse(HttpSession session, OutboundVoiceMessage outboundVoiceMessage, IVRResponseBuilder ivrResponseBuilder) {
        List<String> commands = (List<String>) outboundVoiceMessage.getParameters().get(VOICE_MESSAGE_COMMAND_AUDIO);
        for (String command : commands) {
            OutboxCommand commandObject = (OutboxCommand) applicationContext.getBean(command);
            ivrResponseBuilder.withPlayAudios(commandObject.execute(new IVRSession(session)));
        }
    }

    private void addAudioFilesToBePlayedToResponse(OutboundVoiceMessage outboundVoiceMessage, IVRResponseBuilder ivrResponseBuilder) {
        List<String> audioFiles = (List<String>) outboundVoiceMessage.getParameters().get(AUDIO_FILES_KEY);
        if (audioFiles != null){
            for (String audioFile : audioFiles) {
                ivrResponseBuilder.withPlayAudios(audioFile);
            }
        }
    }

    private void markPreviousMessageAsPlayed(HttpSession session) {
        String lastPlayedMessageId = getLastPlayedMessage(session);
        if (lastPlayedMessageId != null) {
            outboxService.setMessageStatus(lastPlayedMessageId, OutboundVoiceMessageStatus.PLAYED);
        }
    }

    private String postOutboxForwardResponse() {
        IVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        ivrResponseBuilder.withNextUrl(tamaIvrMessage.getText(TamaIVRMessage.POST_OUTBOX_URL));
        return ivrResponseBuilder.createWithDefaultLanguage(tamaIvrMessage, null);

    }


    private void setLastPlayedMessage(HttpSession session, OutboundVoiceMessage outboundVoiceMessage) {
        session.setAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID, outboundVoiceMessage.getId());
    }

    private String getLastPlayedMessage(HttpSession session) {
        return (String) session.getAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID);
    }
}