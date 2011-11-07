package org.motechproject.tama.ivr;

import org.junit.Test;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.web.OutboxController;
import org.motechproject.tama.web.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VoiceMessageResponseFactoryTest {
    @Test
    public void voiceMessageResponse() {
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand = mock(PlayAdherenceTrendFeedbackCommand.class);
        VoiceMessageResponseFactory voiceMessageResponseFactory = new VoiceMessageResponseFactory(playAdherenceTrendFeedbackCommand);

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(OutboxController.VOICE_MESSAGE_COMMAND_AUDIO);
        outboundVoiceMessage.setVoiceMessageType(voiceMessageType);
        outboundVoiceMessage.setCreationTime(DateUtil.now().toDate());
        outboundVoiceMessage.setParameters(new HashMap<String, Object>() {
            {
                put(OutboxController.VOICE_MESSAGE_COMMAND, Arrays.asList("whatever"));
            }
        });

        when(playAdherenceTrendFeedbackCommand.execute(null)).thenReturn(new String[]{"abc"});
        voiceMessageResponseFactory.voiceMessageResponse(null, outboundVoiceMessage, ivrResponseBuilder);
        assertEquals(1, ivrResponseBuilder.getPlayAudios().size());
    }
}
