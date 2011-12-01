package org.motechproject.tamacallflow.ivr.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tamacallflow.ivr.command.AdherenceMessageCommand;
import org.motechproject.tamacallflow.ivr.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tamacallflow.ivr.controller.OutboxController;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VoiceMessageResponseFactoryTest {

    @Mock
    private PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;
    @Mock
    private AdherenceMessageCommand adherenceMessageCommand;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void voiceMessageResponse() {
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        VoiceMessageResponseFactory voiceMessageResponseFactory = new VoiceMessageResponseFactory(playAdherenceTrendFeedbackCommand, adherenceMessageCommand);

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

        when(playAdherenceTrendFeedbackCommand.execute(null)).thenReturn(new String[]{"trend"});
        when(adherenceMessageCommand.execute(null)).thenReturn(new String[]{"percentage"});
        voiceMessageResponseFactory.voiceMessageResponse(null, null, outboundVoiceMessage, ivrResponseBuilder);
        assertEquals(2, ivrResponseBuilder.getPlayAudios().size());
    }
}
