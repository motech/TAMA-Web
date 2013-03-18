package org.motechproject.tama.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PushedOutboxMessageTest {

    @Mock
    private VoiceOutboxService outboxService;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private VoiceMessageResponseFactory messageResponseFactory;

    private PushedOutboxMessage pushedOutboxMessage;

    @Before
    public void setup() {
        initMocks(this);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession httpSession = mock(HttpSession.class);
        Cookies cookies = mock(Cookies.class);

        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
        pushedOutboxMessage = new PushedOutboxMessage(outboxService, messageResponseFactory);
    }

    @Test
    public void shouldPlayAppointReminderMessageIfAnyExists() {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(voiceMessage);

        pushedOutboxMessage.addToResponse(new KookooIVRResponseBuilder(), kookooIVRContext);
        verify(messageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), any(OutboxContext.class), eq(voiceMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldPlayVisitReminderMessageWhenThereAreNoAppointmentReminder() {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(0);
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(voiceMessage);

        pushedOutboxMessage.addToResponse(new KookooIVRResponseBuilder(), kookooIVRContext);
        verify(messageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), any(OutboxContext.class), eq(voiceMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldNotPlayVisitReminderMessageWhenThereAreAppointmentReminders() {
        OutboundVoiceMessage appointmentReminderVoiceMessage = new OutboundVoiceMessage();
        appointmentReminderVoiceMessage.setId("appointmentReminderId");
        OutboundVoiceMessage visitReminderVoiceMessage = new OutboundVoiceMessage();
        visitReminderVoiceMessage.setId("visitReminderId");
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(appointmentReminderVoiceMessage);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(visitReminderVoiceMessage);

        pushedOutboxMessage.addToResponse(new KookooIVRResponseBuilder(), kookooIVRContext);
        verify(messageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), any(OutboxContext.class), eq(appointmentReminderVoiceMessage), any(KookooIVRResponseBuilder.class));
        verifyNoMoreInteractions(messageResponseFactory);
    }

    @Test
    public void shouldPlayAdherenceMessageWhenThereAreNoVisitReminders() {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(0);
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(1);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(voiceMessage);

        pushedOutboxMessage.addToResponse(new KookooIVRResponseBuilder(), kookooIVRContext);
        verify(messageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), any(OutboxContext.class), eq(voiceMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldNotPlayAdherenceMessageWhenThereAreVisitReminders() {
        OutboundVoiceMessage visitReminderVoiceMessage = new OutboundVoiceMessage();
        visitReminderVoiceMessage.setId("visitReminderId");
        OutboundVoiceMessage playAdherenceVoiceMessage = new OutboundVoiceMessage();
        playAdherenceVoiceMessage.setId("playAdherenceVoiceMessage");
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(outboxService.getNumberOfMessages(anyString(), eq(OutboundVoiceMessageStatus.PENDING), eq(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(1);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(visitReminderVoiceMessage);
        when(outboxService.nextMessage(anyString(), anyString(), eq(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(playAdherenceVoiceMessage);

        pushedOutboxMessage.addToResponse(new KookooIVRResponseBuilder(), kookooIVRContext);
        verify(messageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), any(OutboxContext.class), eq(visitReminderVoiceMessage), any(KookooIVRResponseBuilder.class));
        verifyNoMoreInteractions(messageResponseFactory);
    }

    @Test
    public void shouldMarkTheLastPlayedMessageAsReadWhenReado(){

    }
}
