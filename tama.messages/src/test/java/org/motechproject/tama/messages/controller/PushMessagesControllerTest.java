package org.motechproject.tama.messages.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.service.Messages;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PushMessagesControllerTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;
    @Mock
    private Messages messages;
    @Mock
    private PatientService patientService;

    private String patientId = "patientId";

    private PushMessagesController messagesController;

    @Before
    public void setup() {
        initMocks(this);
        setupPatient(CallPreference.FourDayRecall);
        setupSession();
        setupCookies();
        messagesController = new PushMessagesController(messages, patientService);
    }

    private void setupPatient(CallPreference callPreference) {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(callPreference).build();
        PatientReport patientReport = new PatientReport(patient, null, null, null);

        when(httpSession.getAttribute(TAMAIVRContext.PATIENT_ID)).thenReturn(patient.getId());
        when(patientService.getPatientReport(patient.getId())).thenReturn(patientReport);
    }

    private void setupSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
    }

    private void setupCookies() {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldMarkMessageAsReadIfMessageIsAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("healthTip");

        boolean shouldContinue = messagesController.markAsReadAndContinue(kookooIVRContext);

        assertFalse(shouldContinue);
        verify(messages).markAsRead(kookooIVRContext, new PlayedMessage(kookooIVRContext));
    }

    @Test
    public void shouldNotMarkMessageAsReadIfMessageIsNotAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("");
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("");
        when(messages.nextMessage(kookooIVRContext, TAMAMessageType.PUSHED_MESSAGE)).thenReturn(new KookooIVRResponseBuilder());

        boolean shouldContinue = messagesController.markAsReadAndContinue(kookooIVRContext);

        assertFalse(shouldContinue);
        verify(messages, never()).markAsRead(kookooIVRContext, new PlayedMessage(kookooIVRContext));
    }

    @Test
    public void shouldAddMessageToResponse() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("");
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("");
        when(messages.nextMessage(kookooIVRContext, TAMAMessageType.PUSHED_MESSAGE)).thenReturn(new KookooIVRResponseBuilder());

        messagesController.gotDTMF(kookooIVRContext);

        verify(messages).nextMessage(kookooIVRContext, TAMAMessageType.PUSHED_MESSAGE);
    }
}
