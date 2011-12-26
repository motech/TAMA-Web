package org.motechproject.tama.fourdayrecall.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.tama.patient.domain.CallPreference;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class WeeklyVoiceMessageResponseFactoryTest {

    @Mock
    private VoiceMessageResponseFactory voiceMessageResponseFactory;
    private KookooIVRResponseBuilder ivrResponseBuilder;
    private WeeklyVoiceMessageResponseFactory weeklyVoiceMessageResponseFactory;

    @Before
    public void setup() {
        initMocks(this);
        ivrResponseBuilder = new KookooIVRResponseBuilder();
        weeklyVoiceMessageResponseFactory = new WeeklyVoiceMessageResponseFactory(voiceMessageResponseFactory);
    }

    @Test
    public void buildVoiceMessageResponse() {
        weeklyVoiceMessageResponseFactory.buildVoiceMessageResponse(null, null, null, ivrResponseBuilder);

        assertEquals(0, ivrResponseBuilder.getPlayAudios().size());
        verify(voiceMessageResponseFactory).registerOutboxFactory(CallPreference.FourDayRecall, weeklyVoiceMessageResponseFactory);
    }
}
