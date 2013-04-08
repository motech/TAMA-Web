package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.AdherenceTrendMessage;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendMessageProviderTest {

    @Mock
    private PatientOnCall patientOnCall;
    @Mock
    private AdherenceTrendMessage adherenceTrendMessage;
    @Mock
    private TAMAIVRContext context;
    @Mock
    private TreatmentAdvice advice;

    private AdherenceTrendMessageProvider adherenceTrendMessageProvider;

    @Before
    public void setup() {
        initMocks(this);
        when(patientOnCall.getCurrentTreatmentAdvice(context)).thenReturn(advice);
        adherenceTrendMessageProvider = new AdherenceTrendMessageProvider(patientOnCall, adherenceTrendMessage);
    }

    @Test
    public void shouldHaveMessageTreatmentAdviceHasAdherenceTrend() {
        when(adherenceTrendMessage.isValid(any(Patient.class), eq(advice), any(DateTime.class))).thenReturn(true);
        assertTrue(adherenceTrendMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldHaveMessageWhenMessageTypeIsAdherence() {
        when(adherenceTrendMessage.isValid(any(Patient.class), eq(advice), any(DateTime.class))).thenReturn(true);
        assertTrue(adherenceTrendMessageProvider.hasMessage(context, TAMAMessageType.ADHERENCE_TO_ART));
    }

    @Test
    public void shouldNotHaveMessageWhenMessageTypeIsNotAdherenceTrendOrPushMessage() {
        when(adherenceTrendMessage.isValid(any(Patient.class), eq(advice), any(DateTime.class))).thenReturn(true);
        assertFalse(adherenceTrendMessageProvider.hasMessage(context, TAMAMessageType.ART_AND_CD4));
    }

    @Test
    public void shouldNotHaveMessageTreatmentAdviceDoesNotHaveAdherenceTrend() {
        when(advice.hasAdherenceTrend(any(LocalDate.class))).thenReturn(false);
        assertFalse(adherenceTrendMessageProvider.hasMessage(context, org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE));
    }

    @Test
    public void shouldSetMessageTypeWhenGettingNextMessage() {
        when(adherenceTrendMessage.build(any(Patient.class), any(DateTime.class), eq(context))).thenReturn(new KookooIVRResponseBuilder().withPlayAudios("message"));
        adherenceTrendMessageProvider.nextMessage(context);
        verify(context).setTAMAMessageType(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
    }

    @Test
    public void shouldSetLastMessageIdWhenGettingTheNextMessageSoThatTheMessageCouldBeMarkedAsRead() {
        when(adherenceTrendMessage.build(any(Patient.class), any(DateTime.class), eq(context))).thenReturn(new KookooIVRResponseBuilder().withPlayAudios("message"));
        adherenceTrendMessageProvider.nextMessage(context);
        verify(context).lastPlayedMessageId(anyString());
    }
}
