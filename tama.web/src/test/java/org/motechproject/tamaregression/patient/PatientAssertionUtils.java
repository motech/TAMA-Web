package org.motechproject.tamaregression.patient;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class PatientAssertionUtils {

    public static void assertPatientRegistered(TestPatient patient, ShowPatientPage showPatientPage) {
        assertBasicInformation(patient, showPatientPage);
        assertMedicalHistory(patient, showPatientPage);
        assertCallPreferences(patient, showPatientPage);
    }

    public static void assertCallPreferences(TestPatient patient, ShowPatientPage showPatientPage) {
        assertEquals(patient.patientPreferences().passcode(), showPatientPage.getPasscode());
        assertEquals(patient.patientPreferences().callPreference(), showPatientPage.getCallPreference());
    }

    public static void assertMedicalHistory(TestPatient patient, ShowPatientPage showPatientPage) {
        assertEquals(patient.hivMedicalHistory().testReason(), showPatientPage.getHIVTestReason());
        assertEquals(patient.hivMedicalHistory().modeOfTransmission(), showPatientPage.getModeOfTransmission());
        assertEquals("ARV Allergy : arvAllergyDescription", showPatientPage.getAllergyText());
        assertEquals(TAMAConstants.NNRTIRash.DRD.getValue(), showPatientPage.getRashText());
    }

    public static void assertBasicInformation(TestPatient patient, ShowPatientPage showPatientPage) {
        assertEquals(patient.patientId(), showPatientPage.getPatientId());
        assertEquals(patient.mobileNumber(), showPatientPage.getMobileNumber());
        assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()), showPatientPage.getDateOfBirth());
    }
}
