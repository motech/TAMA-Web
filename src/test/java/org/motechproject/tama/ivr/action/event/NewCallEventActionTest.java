package org.motechproject.tama.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.AllPatients;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NewCallEventActionTest extends BaseActionTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private Patient patient;
    @Mock
    private EventService eventService;
    
    private BaseEventAction action;
    public static final String PHONE_NUMBER = "9898982323";

    @Before
    public void setUp() {
        super.setUp();
        action = new NewCallEventAction(messages, userNotFoundAction, eventService, allPatients);
    }

    @Test
    public void shouldHangupIfUserIsNotRegistered() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(allPatients.findByMobileNumber(PHONE_NUMBER)).thenReturn(null);
        when(userNotFoundAction.handle(ivrRequest, request, response)).thenReturn("hangup response");
        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("hangup response", responseXML);
    }

    @Test
    public void shouldInitiateASessionWithRequiredAttributesForANewCallFromAnAuthorizedPatient() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);
        when(allPatients.findByMobileNumber(PHONE_NUMBER)).thenReturn(patient);
        when(patient.isActive()).thenReturn(true);
        when(patient.getId()).thenReturn("patientId");
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");
        when(patient.getIvrLanguage()).thenReturn(ivrLanguage);

        action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVRCallAttribute.CALL_STATE, IVRCallState.COLLECT_PIN);
    }

    @Test
    public void shouldSendThePinRequestXMLResponseForAnAuthorizedPatient() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");
        when(allPatients.findByMobileNumber(PHONE_NUMBER)).thenReturn(patient);
        when(patient.isActive()).thenReturn(true);
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");
        when(patient.getIvrLanguage()).thenReturn(ivrLanguage);
        when(messages.getWav(IVRMessage.SIGNATURE_MUSIC_URL, "en")).thenReturn("http://server/tama.wav");

        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("<response sid=\"unique-call-id\"><collectdtmf><playaudio>http://server/tama.wav</playaudio></collectdtmf></response>", sanitize(responseXML));
    }
    
    @Test
    public void shouldCreateNewCallEvent() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");
        when(session.getAttribute(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn("2323");
        when(allPatients.findByMobileNumber(PHONE_NUMBER)).thenReturn(patient);
        when(patient.isActive()).thenReturn(true);
        IVRLanguage ivrLanguage = IVRLanguage.newIVRLanguage("English", "en");
        when(patient.getIvrLanguage()).thenReturn(ivrLanguage);
        when(messages.getWav(IVRMessage.SIGNATURE_MUSIC_URL, "en")).thenReturn("http://server/tama.wav");

        String responseXML = action.handleInternal(ivrRequest, request, response);
        verify(eventService).publishEvent(any(org.motechproject.eventtracking.domain.Event.class));
    }
}
