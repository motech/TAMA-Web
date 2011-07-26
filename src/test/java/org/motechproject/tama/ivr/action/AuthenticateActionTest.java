package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.repository.Patients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class AuthenticateActionTest extends BaseActionTest {
    private AuthenticateAction authenticateAction;
    @Mock
    private Patients patients;
    @Mock
    private RetryAction retryAction;
    @Mock
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private PillReminderAction userContinueAction;
    @Mock
    private Patient patient;

    private static final String PATIENT_ID = "12345";
    public static final String MOBILE_NO = "9876543210";
    public static final String PASSCODE = "1234";

    @Before
    public void setUp() {
        super.setUp();
        when(patients.get(PATIENT_ID)).thenReturn(patient);
        when(patient.getId()).thenReturn(PATIENT_ID);
        when(session.getAttribute(IVR.Attributes.PATIENT_DOC_ID)).thenReturn(PATIENT_ID);
        authenticateAction = new AuthenticateAction(patients, retryAction, userNotFoundAction, userContinueAction);
    }

    @Test
    public void shouldGoToRetryActionIfPatientPassCodeIsNotValid() {
        IVRRequest ivrRequest = new IVRRequest("sid", MOBILE_NO, IVR.Event.GOT_DTMF.key(), PASSCODE);

        when(request.getSession(false)).thenReturn(session);
        when(retryAction.handle(ivrRequest, request, response)).thenReturn("OK");
        when(patient.authenticateForIVRWith(PASSCODE)).thenReturn(false);

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(retryAction).handle(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldGoToUserContinueActionIfPatientPassCodeIsValid() {
        IVRRequest ivrRequest = new IVRRequest("sid", MOBILE_NO, IVR.Event.GOT_DTMF.key(), PASSCODE);

        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(userContinueAction.handle(ivrRequest, request, response)).thenReturn("OK");
        when(patient.authenticateForIVRWith(PASSCODE)).thenReturn(true);

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(userContinueAction).handle(ivrRequest, request, response);
        verify(session).invalidate();
        verify(session).setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.AUTH_SUCCESS);
        verify(session).setAttribute(IVR.Attributes.PATIENT_DOC_ID, PATIENT_ID);

        assertEquals("OK", handle);
    }

}
