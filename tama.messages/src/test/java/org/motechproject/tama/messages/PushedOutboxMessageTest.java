package org.motechproject.tama.messages;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus.PENDING;
import static org.motechproject.tama.common.TAMAConstants.*;

public class PushedOutboxMessageTest {

    @Mock
    private VoiceOutboxService voiceOutboxService;
    @Mock
    private VoiceMessageResponseFactory voiceMessageResponseFactory;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;

    private String patientId = "patientId";
    private PushedOutboxMessage pushedOutboxMessage;

    @Before
    public void setup() {
        initMocks(this);
        setupSession();
        setupCookies();
        pushedOutboxMessage = new PushedOutboxMessage(voiceOutboxService, voiceMessageResponseFactory);
    }

    private void setupSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(httpSession.getAttribute(TAMAIVRContext.PATIENT_ID)).thenReturn(patientId);
    }

    private void setupCookies() {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldReturnTrueIfAnyMessageIsPresent() {
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), anyString())).thenReturn(1);
        assertTrue(pushedOutboxMessage.hasAnyMessage(kookooIVRContext));
    }

    @Test
    public void shouldReturnFalseIfNoMessageIsPresent() {
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), anyString())).thenReturn(0);
        assertFalse(pushedOutboxMessage.hasAnyMessage(kookooIVRContext));
    }

    @Test
    public void shouldAddAppointmentReminderVoiceMessageToResponseIfPresent() {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        OutboxContext outboxContext = new OutboxContext(kookooIVRContext);

        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(voiceOutboxService.nextMessage(null, patientId, PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(outboundVoiceMessage);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(voiceMessageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), eq(outboxContext), eq(outboundVoiceMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldNotAddAppointmentReminderVoiceMessageToResponseIfNotPresent() {
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(0);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verifyZeroInteractions(voiceMessageResponseFactory);
    }

    @Test
    public void shouldAddVisitReminderVoiceMessageToResponse() {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        OutboxContext outboxContext = new OutboxContext(kookooIVRContext);

        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(voiceOutboxService.nextMessage(null, patientId, VISIT_REMINDER_VOICE_MESSAGE)).thenReturn(outboundVoiceMessage);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(voiceMessageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), eq(outboxContext), eq(outboundVoiceMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldNotAddVisitReminderVoiceMessageToResponseIfNotPresent() {
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(0);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verifyZeroInteractions(voiceMessageResponseFactory);
    }

    @Test
    public void shouldAddVoiceMessageCommandToResponse() {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        OutboxContext outboxContext = new OutboxContext(kookooIVRContext);

        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(1);
        when(voiceOutboxService.nextMessage(null, patientId, VOICE_MESSAGE_COMMAND_AUDIO)).thenReturn(outboundVoiceMessage);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(voiceMessageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), eq(outboxContext), eq(outboundVoiceMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldNotAddVoiceMessageCommandToResponseIfNotPresent() {
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(0);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verifyZeroInteractions(voiceMessageResponseFactory);
    }

    @Test
    public void shouldGiveHighestPriorityToAppointmentReminder() {
        OutboxContext outboxContext = new OutboxContext(kookooIVRContext);
        OutboundVoiceMessage appointmentReminderMessage = new OutboundVoiceMessage();
        appointmentReminderMessage.setId("id");

        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(1);

        when(voiceOutboxService.nextMessage(null, patientId, PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(appointmentReminderMessage);
        when(voiceOutboxService.nextMessage(null, patientId, VISIT_REMINDER_VOICE_MESSAGE)).thenReturn(new OutboundVoiceMessage());
        when(voiceOutboxService.nextMessage(null, patientId, VOICE_MESSAGE_COMMAND_AUDIO)).thenReturn(new OutboundVoiceMessage());

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(voiceMessageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), eq(outboxContext), eq(appointmentReminderMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldGiveVisitReminderPriorityOverVoiceMessage() {
        OutboxContext outboxContext = new OutboxContext(kookooIVRContext);
        OutboundVoiceMessage visitReminderMessage = new OutboundVoiceMessage();
        visitReminderMessage.setId("id");

        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VISIT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(VOICE_MESSAGE_COMMAND_AUDIO))).thenReturn(1);

        when(voiceOutboxService.nextMessage(null, patientId, VISIT_REMINDER_VOICE_MESSAGE)).thenReturn(visitReminderMessage);
        when(voiceOutboxService.nextMessage(null, patientId, VOICE_MESSAGE_COMMAND_AUDIO)).thenReturn(new OutboundVoiceMessage());

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(voiceMessageResponseFactory).voiceMessageResponse(eq(kookooIVRContext), eq(outboxContext), eq(visitReminderMessage), any(KookooIVRResponseBuilder.class));
    }

    @Test
    public void shouldAddLastPlayedMessageToCookieIfLastPlayedMessageIsPresent() {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setId("id");

        when(voiceOutboxService.getNumberOfMessages(eq(patientId), eq(PENDING), eq(PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE))).thenReturn(1);
        when(voiceOutboxService.nextMessage(null, patientId, PUSHED_APPOINTMENT_REMINDER_VOICE_MESSAGE)).thenReturn(outboundVoiceMessage);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(cookies).add(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID, "id");
    }

    @Test
    public void shouldNotAddLastPlayedMessageToCookieIfLastPlayedMessageIsNotPresent() {
        when(voiceOutboxService.nextMessage(anyString(), eq(patientId), anyString())).thenReturn(null);

        pushedOutboxMessage.getResponse(kookooIVRContext);

        verify(cookies, never()).add(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID, "id");
    }
}
