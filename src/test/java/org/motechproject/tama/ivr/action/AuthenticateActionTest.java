package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallState;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.action.pillreminder.DosageMenuAction;
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
    private DosageMenuAction userContinueAction;
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
        when(session.getAttribute(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn(PATIENT_ID);
        authenticateAction = new AuthenticateAction(patients, retryAction, userNotFoundAction, userContinueAction);
    }

    @Test
    public void shouldGoToRetryActionIfPatientPassCodeIsNotValid() {
        IVRRequest ivrRequest = new IVRRequest("sid", MOBILE_NO, IVREvent.GOT_DTMF.key(), PASSCODE);

        when(request.getSession(false)).thenReturn(session);
        when(retryAction.handle(ivrRequest, request, response)).thenReturn("OK");
        when(patient.authenticatedWith(PASSCODE)).thenReturn(false);

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(retryAction).handle(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldGoToUserContinueActionIfPatientPassCodeIsValid() {
        IVRRequest ivrRequest = new IVRRequest("sid", MOBILE_NO, IVREvent.GOT_DTMF.key(), PASSCODE);

        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(userContinueAction.handle(ivrRequest, request, response)).thenReturn("OK");
        when(patient.authenticatedWith(PASSCODE)).thenReturn(true);

        String handle = authenticateAction.handle(ivrRequest, request, response);

        verify(userContinueAction).handle(ivrRequest, request, response);
        verify(session).invalidate();
        verify(session).setAttribute(IVRCallAttribute.CALL_STATE, IVRCallState.AUTH_SUCCESS);
        verify(session).setAttribute(IVRCallAttribute.PATIENT_DOC_ID, PATIENT_ID);

        assertEquals("OK", handle);
    }

}
