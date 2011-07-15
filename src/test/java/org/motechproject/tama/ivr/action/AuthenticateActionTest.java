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
import static org.mockito.MockitoAnnotations.initMocks;


public class AuthenticateActionTest extends BaseActionTest {
    private AuthenticateAction authenticateAction;
    @Mock
    private Patients patients;
    @Mock
    private RetryAction retryAction;
    @Mock
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private UserContinueAction userContinueAction;

    @Before
    public void setUp() {
        initMocks(this);
        authenticateAction = new AuthenticateAction(patients, retryAction, userNotFoundAction, userContinueAction);
    }

    @Test
    public void shouldGoToUserNotFoundActionIfPatientNotFoundByMobileNumber() {
        String mobileNo = "9876543210";
        IVRRequest ivrRequest = new IVRRequest();

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(mobileNo);
        when(patients.findByMobileNumber(mobileNo)).thenReturn(null);
        when(userNotFoundAction.handle(ivrRequest, request, response)).thenReturn("OK");

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(userNotFoundAction).handle(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldGoToRetryActionIfPatientPassCodeIsNotValid() {
        String mobileNo = "9876543210";
        String passcode = "1234";
        Patient patient = mock(Patient.class);
        IVRRequest ivrRequest = new IVRRequest("sid", mobileNo, IVR.Event.GOT_DTMF.key(), passcode);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(mobileNo);
        when(patients.findByMobileNumber(mobileNo)).thenReturn(patient);
        when(retryAction.handle(ivrRequest, request, response)).thenReturn("OK");
        when(patient.hasPasscode(passcode)).thenReturn(false);

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(retryAction).handle(ivrRequest, request, response);
        assertEquals("OK", handle);
    }


    @Test
    public void shouldGoToUserContinueActionIfPatientPassCodeIsValid() {
        String mobileNo = "9876543210";
        String passcode = "1234";
        Patient patient = mock(Patient.class);

        IVRRequest ivrRequest = new IVRRequest("sid", mobileNo, IVR.Event.GOT_DTMF.key(), passcode);

        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(mobileNo);
        when(patients.findByMobileNumber(mobileNo)).thenReturn(patient);
        when(userContinueAction.handle(ivrRequest, request, response)).thenReturn("OK");
        when(patient.hasPasscode(passcode)).thenReturn(true);
        when(patient.getId()).thenReturn("P1");

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(userContinueAction).handle(ivrRequest, request, response);
        verify(session).invalidate();
        verify(session).setAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID, patient.getId());
        verify(session).setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.AUTH_SUCCESS);

        assertEquals("OK", handle);
    }

}
