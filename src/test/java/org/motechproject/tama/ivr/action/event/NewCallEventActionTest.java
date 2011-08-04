package org.motechproject.tama.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.*;
import org.motechproject.tama.ivr.action.UserNotFoundAction;
import org.motechproject.tama.repository.Patients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewCallEventActionTest extends BaseActionTest {
    @Mock
    private Patients patients;
    @Mock
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private Patient patient;
    @Mock
    private PillReminderService pillReminderService;
    private NewCallEventAction action;
    public static final String PHONE_NUMBER = "9898982323";

    @Before
    public void setUp() {
        super.setUp();
        action = new NewCallEventAction(pillReminderService, messages, patients, userNotFoundAction);
    }

    @Test
    public void shouldHangupIfUserIsNotRegistered() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(patients.findByMobileNumber(PHONE_NUMBER)).thenReturn(null);
        when(userNotFoundAction.handle(ivrRequest, request, response)).thenReturn("hangup response");
        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("hangup response", responseXML);
    }

    @Test
    public void shouldHangupIfPatientIsNotActive() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(patients.findByMobileNumber(PHONE_NUMBER)).thenReturn(patient);
        when(patient.isActive()).thenReturn(false);
        when(userNotFoundAction.handle(ivrRequest, request, response)).thenReturn("hangup response");
        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("hangup response", responseXML);
    }

    @Test
    public void shouldInitiateASessionWithRequiredAttributesForANewCallFromAnAuthorizedPatient() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);
        when(patients.findByMobileNumber(PHONE_NUMBER)).thenReturn(patient);
        when(patient.isActive()).thenReturn(true);
        when(patient.getId()).thenReturn("patientId");
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(null, "patientId", 2, 5, null);
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimenResponse);

        action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVRCallAttribute.CALL_STATE, IVRCallState.COLLECT_PIN);
        verify(session).setAttribute(IVRCallAttribute.PATIENT_DOC_ID, "patientId");
        verify(session).setAttribute(IVRCallAttribute.REGIMEN_FOR_PATIENT, pillRegimenResponse);
    }

    @Test
    public void shouldSendThePinRequestXMLResponseForAnAuthorizedPatient() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", PHONE_NUMBER, IVREvent.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);
        when(patients.findByMobileNumber(PHONE_NUMBER)).thenReturn(patient);
        when(patient.isActive()).thenReturn(true);
        when(messages.getWav(IVRMessage.SIGNATURE_MUSIC_URL)).thenReturn("http://server/tama.wav");

        String responseXML = action.handle(ivrRequest, request, response);
        assertEquals("<response sid=\"unique-call-id\"><collectdtmf><playaudio>http://server/tama.wav</playaudio></collectdtmf></response>", sanitize(responseXML));
    }

}
