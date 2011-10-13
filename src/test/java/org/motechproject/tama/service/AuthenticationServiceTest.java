package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.IVRAuthenticationStatus;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllIVRCallAudits;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthenticationServiceTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllIVRCallAudits allIVRCallAudits;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    private AuthenticationService authenticationService;

    private String phoneNumber = "9999";
    private String passcode = "1234";

    @Before
    public void setUp() {
        initMocks(this);
        authenticationService = new AuthenticationService(allPatients, allIVRCallAudits);
        authenticationService.maxNoOfAttempts = 3;
    }

    @Test
    public void patientIsNotActiveWhenItDoesntHaveTreatmentAdvice() {
        Patient patient = new Patient();
        patient.setStatus(Patient.Status.Inactive);
        String patientId = "43245454354";
        patient.setId(patientId);

        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(patient);
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(null);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 1, "123");
        assertEquals(false, ivrAuthenticationStatus.isActive());
    }

    @Test
    public void patientIsAllowedRetryWhenPasscodeFails() {
        Patient patient = new Patient();
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(null);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 1, "123");
        assertEquals(false, ivrAuthenticationStatus.isAuthenticated());
        assertEquals(true, ivrAuthenticationStatus.doAllowRetry());
    }

    @Test
    public void donotAllowRetriesAfterMaxLimit() {
        Patient patient = new Patient();
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(null);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 2, "123");
        assertEquals(true, ivrAuthenticationStatus.doAllowRetry());
        ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 3, "123");
        assertEquals(false, ivrAuthenticationStatus.isAuthenticated());
        assertEquals(false, ivrAuthenticationStatus.doAllowRetry());
    }

    @Test
    public void allowIncrementLoginAttemptCountWhenNoInputIsSent() {
        String invalidPassCode = "";
        Patient patient = new Patient();
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, invalidPassCode)).thenReturn(null);

        int attemptNumber = 2;
        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, invalidPassCode, attemptNumber, "123");
        assertEquals(attemptNumber - 1, ivrAuthenticationStatus.loginAttemptNumber());
    }
}
