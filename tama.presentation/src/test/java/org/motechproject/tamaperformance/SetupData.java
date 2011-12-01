package org.motechproject.tamaperformance;

import org.junit.Test;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicDataService;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

public class SetupData extends BaseIVRTest {
    @Test
    public void setup() {
        TestSample testSample = new TestSample();

        for (TestClinic clinic : testSample.clinics) {
            new ClinicDataService(webDriver).create(clinic);
        }

        for (int i = 0; i < testSample.clinicians.size(); i++) {
            TestClinician clinician = testSample.clinicians.get(i);

            new ClinicianDataService(webDriver).create(clinician);

            createPatientWithARTRegimen(testSample.patients.get(i * 2), testSample.treatmentAdvice, clinician);
            createPatientWithARTRegimen(testSample.patients.get(i * 2 + 1), testSample.treatmentAdvice, clinician);
        }
    }

    private void createPatientWithARTRegimen(TestPatient patient, TestTreatmentAdvice treatmentAdvice, TestClinician clinician) {
        new PatientDataService(webDriver).registerAndActivate(patient, clinician);
        new PatientDataService(webDriver).createARTRegimen(treatmentAdvice, patient, clinician);
    }
}