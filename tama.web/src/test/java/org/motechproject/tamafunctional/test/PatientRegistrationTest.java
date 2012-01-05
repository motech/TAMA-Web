package org.motechproject.tamafunctional.test;

import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestPatientPreferences;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class PatientRegistrationTest extends BaseTest {

    @Test
    public void onDailyReminder() {
        TestClinician clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        TestPatient patient = TestPatient.withMandatory();

        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnDailyPillReminder(patient);

        assertEquals(patient.patientId(), showPatientPage.getPatientId());
        assertEquals(patient.mobileNumber(), showPatientPage.getMobileNumber());
        assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()), showPatientPage.getDateOfBirth());

        assertEquals(patient.hivMedicalHistory().testReason(), showPatientPage.getHIVTestReason());
        assertEquals(patient.hivMedicalHistory().modeOfTransmission(), showPatientPage.getModeOfTransmission());
        assertEquals("ARV Allergy : arvAllergyDescription", showPatientPage.getAllergyText());
        assertEquals(TAMAConstants.NNRTIRash.DRD.getValue(), showPatientPage.getRashText());

        assertEquals(patient.patientPreferences().passcode(), showPatientPage.getPasscode());
        assertEquals(patient.patientPreferences().callPreference(), showPatientPage.getCallPreference());
        showPatientPage.logout();
    }

    @Test
    public void onFourDayRecall() {
        TestClinician clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        TestPatientPreferences patientPreferences = TestPatientPreferences.withMandatory();
        patientPreferences.callPreference(TestPatientPreferences.CallPreference.WEEKLY_CALL);
        TestPatient patient = TestPatient.withMandatory().patientPreferences(patientPreferences);

        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnWeekly(patient);

        assertEquals(patient.patientId(), showPatientPage.getPatientId());
        assertEquals(patient.mobileNumber(), showPatientPage.getMobileNumber());
        assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()), showPatientPage.getDateOfBirth());

        assertEquals(patient.hivMedicalHistory().testReason(), showPatientPage.getHIVTestReason());
        assertEquals(patient.hivMedicalHistory().modeOfTransmission(), showPatientPage.getModeOfTransmission());
        assertEquals("ARV Allergy : arvAllergyDescription", showPatientPage.getAllergyText());
        assertEquals(TAMAConstants.NNRTIRash.DRD.getValue(), showPatientPage.getRashText());

        assertEquals(patient.patientPreferences().passcode(), showPatientPage.getPasscode());
        assertEquals(patient.patientPreferences().callPreference(), showPatientPage.getCallPreference());
        showPatientPage.logout();
    }
}
