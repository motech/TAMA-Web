package org.motechproject.tama.messages.provider;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendMessageProviderTest {

    @Mock
    private PatientOnCall patientOnCall;
    @Mock
    private PlayAdherenceTrendFeedbackCommand adherenceTrendFeedback;
    @Mock
    private TAMAIVRContext context;
    @Mock
    private TreatmentAdvice advice;

    private AdherenceTrendMessageProvider adherenceTrendMessageProvider;

    @Before
    public void setup() {
        initMocks(this);
        when(patientOnCall.getCurrentTreatmentAdvice(context)).thenReturn(advice);
        adherenceTrendMessageProvider = new AdherenceTrendMessageProvider(patientOnCall, adherenceTrendFeedback);
    }

    @Test
    public void shouldHaveMessageTreatmentAdviceHasAdherenceTrend() {
        when(advice.hasAdherenceTrend(any(LocalDate.class))).thenReturn(true);
        assertTrue(adherenceTrendMessageProvider.hasMessage(context));
    }

    @Test
    public void shouldNotHaveMessageTreatmentAdviceDoesNotHaveAdherenceTrend() {
        when(advice.hasAdherenceTrend(any(LocalDate.class))).thenReturn(false);
        assertFalse(adherenceTrendMessageProvider.hasMessage(context));
    }

    @Test
    public void shouldSetMessageTypeWhenGettingNextMessage() {
        when(adherenceTrendFeedback.executeCommand(context)).thenReturn(new String[]{"message"});
        adherenceTrendMessageProvider.nextMessage(context);
        verify(context).setTAMAMessageType(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
    }

    @Test
    public void shouldSetLastMessageIdWhenGettingTheNextMessageSoThatTheMessageCouldBeMarkedAsRead() {
        when(adherenceTrendFeedback.executeCommand(context)).thenReturn(new String[]{"message"});
        adherenceTrendMessageProvider.nextMessage(context);
        verify(context).lastPlayedMessageId(anyString());
    }
}
