package org.motechproject.tamafunctional.test;

import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicanDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.openqa.selenium.support.PageFactory;

import static junit.framework.Assert.assertEquals;

public class PatientARTRegimenTest extends BaseTest {
    @Test
    public void testPatientARTRegimen() {
        TestClinician clinician = TestClinician.withMandatory();
        new ClinicanDataService(webDriver).createWithClinc(clinician);

        TestPatient patient = TestPatient.withMandatory(clinician.clinic());
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.forMorning(), TestDrugDosage.forEvening());
        patientDataService.setupARTRegimen(treatmentAdvice, patient, clinician);

        TestTreatmentAdvice savedTreatmentAdvice = patientDataService.getTreatmentAdvice(patient, clinician);
        assertEquals(savedTreatmentAdvice.regimenName(), treatmentAdvice.regimenName());
        assertEquals(savedTreatmentAdvice.drugCompositionName(), treatmentAdvice.drugCompositionName());
    }
}
