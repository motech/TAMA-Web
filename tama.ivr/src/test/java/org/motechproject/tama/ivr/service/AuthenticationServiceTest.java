package org.motechproject.tama.ivr.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.IVRAuthenticationStatus;
import org.motechproject.tama.ivr.repository.AllIVRCallAudits;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthenticationServiceTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllIVRCallAudits allIVRCallAudits;
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
    public void patientIsAllowedAccessWhenSuspended() {
        String patientId = "43245454354";
        Patient patient = patient(patientId);
        patient.setStatus(Status.Suspended);

        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(patient);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 1, "123", false);
        assertEquals(true, ivrAuthenticationStatus.allowCall());
    }

    @Test
    public void patientIsAllowedAccessWhenActive() {
        String patientId = "43245454354";
        Patient patient = patient(patientId);
        patient.setStatus(Status.Active);

        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(patient);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 1, "123", false);
        assertEquals(true, ivrAuthenticationStatus.allowCall());
    }

    @Test
    public void patientIsAllowedAccessWhenNotActive() {
        String patientId = "43245454354";
        Patient patient = patient(patientId);
        patient.setStatus(Status.Patient_Withdraws_Consent);

        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(patient);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 1, "123", false);
        assertEquals(false, ivrAuthenticationStatus.allowCall());
    }

    @Test
    public void patientIsAllowedRetryWhenPasscodeFails() {
        Patient patient = patient("foo");
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(null);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 1, "123", false);
        assertEquals(false, ivrAuthenticationStatus.isAuthenticated());
        assertEquals(true, ivrAuthenticationStatus.doAllowRetry());
    }

    @Test
    public void donotAllowRetriesAfterMaxLimit() {
        Patient patient = patient("foo");
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(null);

        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 2, "123", false);
        assertEquals(true, ivrAuthenticationStatus.doAllowRetry());
        ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 3, "123", false);
        assertEquals(false, ivrAuthenticationStatus.isAuthenticated());
        assertEquals(false, ivrAuthenticationStatus.doAllowRetry());
    }

    @Test
    public void allowIncrementLoginAttemptCountWhenNoInputIsSent() {
        String invalidPassCode = "";
        Patient patient = patient("foo");
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, invalidPassCode)).thenReturn(null);

        int attemptNumber = 2;
        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, invalidPassCode, attemptNumber, "123", false);
        assertEquals(attemptNumber - 1, ivrAuthenticationStatus.loginAttemptNumber());
    }

    @Test
    public void multiplePatientsWithSamePin() {
        Patient firstPatient = patient("p1");
        Patient secondPatient = patient("p2");
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(firstPatient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(secondPatient);
        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 2, "123", false);
        assertEquals(secondPatient.getId(), ivrAuthenticationStatus.patientId());
    }

    @Test
    public void successfulLogin_ForIncomingCall() {
        Patient patient = patient("p1");
        patient.activate();
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(patient);
        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 2, "123", false);
        assertEquals(true, ivrAuthenticationStatus.isAuthenticated());
        assertEquals(true, ivrAuthenticationStatus.allowCall());
        assertEquals(patient.getPatientPreferences().getIvrLanguage().getCode(), ivrAuthenticationStatus.language());
    }

    @Test
    public void successfulLogin_ForOutgoingCall() {
        Patient patient = patient("p1");
        patient.activate();
        when(allPatients.findByMobileNumber(phoneNumber)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(phoneNumber, passcode)).thenReturn(patient);
        IVRAuthenticationStatus ivrAuthenticationStatus = authenticationService.checkAccess(phoneNumber, passcode, 2, "123", true);
        assertEquals(true, ivrAuthenticationStatus.isAuthenticated());
        assertEquals(true, ivrAuthenticationStatus.allowCall());
        assertEquals(patient.getPatientPreferences().getIvrLanguage().getCode(), ivrAuthenticationStatus.language());
    }

    private Patient patient(String id) {
        Patient patient = new Patient();
        patient.setId(id);
        PatientPreferences patientPreferences = new PatientPreferences();
        IVRLanguage ivrLanguage = new IVRLanguage();
        ivrLanguage.setCode("tl");
        patientPreferences.setIvrLanguage(ivrLanguage);
        patient.setPatientPreferences(patientPreferences);
        return patient;
    }
}
