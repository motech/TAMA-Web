package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;

public class IVRControllerTest {

    public static final String PASSCODE = "12345";
    public static final String MOBILE_NUMBER = "9898982323";
    private IVRController controller;

    @Mock
    private Patients patients;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private Properties properties;

    private Patient patientFromDb;

    @Before
    public void setup() {
        initMocks(this);
        controller = new IVRController(patients, properties);

        patientFromDb = new PatientBuilder()
                .withDefaults()
                .withMobileNumber(MOBILE_NUMBER)
                .withPasscode(PASSCODE)
                .build();
    }

    @Test
    public void shouldCreateANewSessionForTheFirstRequest_Authentication() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);

        controller.authenticate(MOBILE_NUMBER, PASSCODE,  request);
        verify(request, times(2)).getSession();
    }

    @Test
    public void ShouldCreateANewSessionForTheFirstRequest_Unauthentication() {
        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);
        when(properties.getProperty(IVRController.MAX_NUM_OF_IVR_RETRIES_KEY)).thenReturn(String.valueOf(1));

        controller.authenticate(MOBILE_NUMBER, "wrong" + PASSCODE, request);
        verify(request, times(1)).getSession();
    }

    @Test
    public void shouldAuthenticateAPatientWithCorrectPasscode() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);

        String authStatus = controller.authenticate(MOBILE_NUMBER, PASSCODE, request);

        assertEquals(TAMAConstants.AUTH_STATUS.AUTHENTICATED.getValue(), authStatus);
    }

    @Test
    public void shouldNotAuthenticateAPatientWithIncorrectPasscode() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);
        when(properties.getProperty(IVRController.MAX_NUM_OF_IVR_RETRIES_KEY)).thenReturn(String.valueOf(1));

        String authStatus = controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);

        assertEquals(TAMAConstants.AUTH_STATUS.UNAUTHENTICATED.getValue(), authStatus);
    }

    @Test
    public void shouldStoreNumberOfAuthenticationRetriesInTheSession() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);
        when(properties.getProperty(IVRController.MAX_NUM_OF_IVR_RETRIES_KEY)).thenReturn(String.valueOf(3));

        controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);
        verify(session).setAttribute(IVRController.NUM_OF_RETRIES, 1);

        when(session.getAttribute(IVRController.NUM_OF_RETRIES)).thenReturn(1);
        controller.authenticate(MOBILE_NUMBER, "wrong" + PASSCODE, request);
        verify(session).setAttribute(IVRController.NUM_OF_RETRIES, 2);
    }

    @Test
    public void shouldStorePatientDocumentIdInTheSessionWhenAuthenticated() {

        String patientDocumentId = "1234";
        patientFromDb.setId(patientDocumentId);

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);
        when(properties.getProperty(IVRController.MAX_NUM_OF_IVR_RETRIES_KEY)).thenReturn(String.valueOf(2));

        controller.authenticate(MOBILE_NUMBER, PASSCODE, request);
        verify(session).setAttribute(IVRController.PATIENT_DOCUMENT_ID, patientDocumentId);
    }

    @Test
    public void shouldNotStorePatientDocumentIdInTheSessionWhenUnauthenticated() {

        String patientDocumentId = "1234";
        patientFromDb.setId(patientDocumentId);

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);
        when(properties.getProperty(IVRController.MAX_NUM_OF_IVR_RETRIES_KEY)).thenReturn(String.valueOf(2));

        controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);
        verify(session, never()).setAttribute(IVRController.PATIENT_DOCUMENT_ID, patientDocumentId);
    }

    @Test
    public void shouldDiscardTheOldSessionAndCreateANewOneAfterSuccessfulAuthentication() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);

        controller.authenticate(MOBILE_NUMBER, PASSCODE,  request);

        InOrder inOrder = inOrder(session, request);
        inOrder.verify(session).invalidate();
        inOrder.verify(request).getSession();
    }
    
    @Test
    public void shouldInvalidateSessionAfterMaximumRetries() {
        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(IVRController.NUM_OF_RETRIES))
                .thenReturn(null)
                .thenReturn(1)
                .thenReturn(2);
        when(properties.getProperty(IVRController.MAX_NUM_OF_IVR_RETRIES_KEY)).thenReturn(String.valueOf(3));

        controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);
        verify(session, never()).invalidate();

        controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);
        verify(session, never()).invalidate();

        controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);
        verify(session).invalidate();
    }
}
