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
    }

    private void setupPatient() {
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = setupVariableDosages();
        enrollPatientIntoRegimen(drugDosages);
    }

    private void enrollPatientIntoRegimen(TestDrugDosage[] drugDosages) {
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
    }

    private TestDrugDosage[] setupVariableDosages() {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        drugDosages[1].setVariableDose("2");
        return drugDosages;
    }

    @Test
    public void testAdherence() {
//        tamaDateTimeService.adjustDateTime(DateUtil.now().minusWeeks(2));
    }
}
