package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

    private Patient patientFromDb;

    @Before
    public void setup() {
        initMocks(this);
        controller = new IVRController(patients);

        patientFromDb = new PatientBuilder()
                .withDefaults()
                .withMobileNumber(MOBILE_NUMBER)
                .withPasscode(PASSCODE)
                .build();
    }

    @Test
    public void shouldCreateANewSessionForTheFirstRequest() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);

        controller.authenticate(MOBILE_NUMBER, PASSCODE,  request);

        verify(request).getSession(true);
    }

    @Test
    public void shouldAuthenticateAPatientWithCorrectPasscode() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);

        String authStatus = controller.authenticate(MOBILE_NUMBER, PASSCODE, request);

        assertEquals(TAMAConstants.AUTH_STATUS.AUTHENTICATED.getValue(), authStatus);
    }

    @Test
    public void shouldNotAuthenticateAPatientWithIncorrectPasscode() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession(true)).thenReturn(session);

        String authStatus = controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);

        assertEquals(TAMAConstants.AUTH_STATUS.UNAUTHENTICATED.getValue(), authStatus);
    }

    @Test
    public void shouldStoreNumberOfAuthenticationRetriesInTheSession() {

        when(patients.findByMobileNumber(MOBILE_NUMBER)).thenReturn(patientFromDb);
        when(request.getSession(true)).thenReturn(session);

        controller.authenticate(MOBILE_NUMBER, "wrong"+PASSCODE, request);
        verify(session).setAttribute(IVRController.NUM_OF_RETRIES, 1);

        when(session.getAttribute(IVRController.NUM_OF_RETRIES)).thenReturn(1);
        controller.authenticate(MOBILE_NUMBER, "wrong" + PASSCODE, request);
        verify(session).setAttribute(IVRController.NUM_OF_RETRIES, 2);
    }
    
    @Test
    public void shouldAllowMaximumRetriesAsPerTheConfiguredValue() {
        
    }
}
