package org.motechproject.tama.messages.push;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessagesTest {

    @Mock
    private PatientService patientService;
    @Mock
    private HealthTipMessage pushedHealthTipsMessage;
    @Mock
    private OutboxMessage outboxMessage;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;

    private Messages messages;

    @Before
    public void setup() {
        initMocks(this);
        setupPatient(CallPreference.FourDayRecall);
        setupCookies();
        setupSession();
        messages = new Messages(outboxMessage, pushedHealthTipsMessage, patientService);
    }

    @Test
    public void fourDayRecallPatientsHaveADefaultMessage() {
        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().contains(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY));
    }

    @Test
    public void shouldAddOutboxMessageToResponse() {
        KookooIVRResponseBuilder outboxMessage = new KookooIVRResponseBuilder().withPlayAudios("outboxMessage");

        when(this.outboxMessage.hasAnyMessage(kookooIVRContext)).thenReturn(true);
        when(this.outboxMessage.getResponse(kookooIVRContext)).thenReturn(outboxMessage);

        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().contains("outboxMessage"));
    }

    @Test
    public void shouldAddHealthTipsToResponse() {
        KookooIVRResponseBuilder healthTipMessage = new KookooIVRResponseBuilder().withPlayAudios("healthTipMessage");

        when(pushedHealthTipsMessage.hasAnyMessage(kookooIVRContext)).thenReturn(true);
        when(pushedHealthTipsMessage.getResponse(kookooIVRContext)).thenReturn(healthTipMessage);

        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().contains("healthTipMessage"));
    }

    @Test
    public void shouldReturnEmptyResponseWhenThereAreNoMessages() {
        setupPatient(CallPreference.DailyPillReminder);
        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().isEmpty());
    }

    @Test
    public void shouldMarkOutboxMessageAsReadWhenLastPlayedMessageIsOutboxMessage() {
        PlayedMessage playedMessage = new PlayedMessage(kookooIVRContext);

        messages.markAsRead(kookooIVRContext, playedMessage);
        verify(outboxMessage).markAsRead(kookooIVRContext);
    }

    @Test
    public void shouldMarkHealthTipAsReadWhenLastPlayedMessageIsHealthTip() {
        PlayedMessage playedMessage = new PlayedMessage(kookooIVRContext);
        String playedHealthTip = "healthTip";

        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn(playedHealthTip);
        messages.markAsRead(kookooIVRContext, playedMessage);
        verify(pushedHealthTipsMessage).markAsRead(anyString(), eq(playedHealthTip));
    }

    private void setupPatient(CallPreference callPreference) {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(callPreference).build();
        PatientReport patientReport = new PatientReport(patient, null, null, null);

        when(httpSession.getAttribute(TAMAIVRContext.PATIENT_ID)).thenReturn(patient.getId());
        when(patientService.getPatientReport(patient.getId())).thenReturn(patientReport);
    }

    private void setupCookies() {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    private void setupSession() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
    }
}
