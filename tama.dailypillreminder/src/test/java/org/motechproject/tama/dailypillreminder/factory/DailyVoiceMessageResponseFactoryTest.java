package org.motechproject.tama.dailypillreminder.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.command.AdherenceMessageCommand;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DailyVoiceMessageResponseFactoryTest {

    @Mock
    private PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;
    @Mock
    private VoiceMessageResponseFactory voiceMessageResponseFactory;
    @Mock
    private AdherenceMessageCommand adherenceMessageCommand;
    @Mock
    private OutboxContext outboxContext;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void buildVoiceMessageResponse() {
        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        DailyVoiceMessageResponseFactory dailyVoiceMessageResponseFactory = new DailyVoiceMessageResponseFactory(voiceMessageResponseFactory, playAdherenceTrendFeedbackCommand, adherenceMessageCommand);

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        outboundVoiceMessage.setVoiceMessageType(voiceMessageType);
        outboundVoiceMessage.setCreationTime(DateUtil.now().toDate());
        outboundVoiceMessage.setParameters(new HashMap<String, Object>() {
            {
                put(TAMAConstants.VOICE_MESSAGE_COMMAND, Arrays.asList("whatever"));
            }
        });

        when(playAdherenceTrendFeedbackCommand.execute(null)).thenReturn(new String[]{"trend"});
        when(adherenceMessageCommand.execute(null)).thenReturn(new String[]{"percentage"});
        dailyVoiceMessageResponseFactory.buildVoiceMessageResponse(null, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
        assertEquals(2, ivrResponseBuilder.getPlayAudios().size());
        verify(voiceMessageResponseFactory).registerOutboxFactory(CallPreference.DailyPillReminder, dailyVoiceMessageResponseFactory);
    }
}
