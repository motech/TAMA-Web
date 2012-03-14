package org.motechproject.tamaregression.serial;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

public class VariableDosageTest extends BaseIVRTest {

    private TestPatient patient;
    private TestClinician clinician;
    private TAMADateTimeService tamaDateTimeService;

    @Before
    public void setUp() {
        super.setUp();
        tamaDateTimeService = new TAMADateTimeService(webClient);
        setupPatient();
        caller = caller(patient);
    }

    private void setupPatient() {
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        enrollPatientIntoRegimen();
    }

    private void enrollPatientIntoRegimen() {
        TestDrugDosage[] drugDosages = setupVariableDosages();

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages).regimenName("d4T + 3TC + NVP").drugCompositionName("d4T+3TC+NVP");
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
    }

    private TestDrugDosage[] setupVariableDosages() {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Triomune");
        drugDosages[0].setVariableDose("2");
        return drugDosages;
    }

    @Test
    public void testAdherence() {
    }
}
