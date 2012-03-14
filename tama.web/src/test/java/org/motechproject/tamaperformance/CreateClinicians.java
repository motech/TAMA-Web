package org.motechproject.tamaperformance;

import org.junit.Test;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicDataService;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

public class CreateClinicians extends BaseIVRTest {

    @Test
    public void createClinicians() {

        ClinicDataService clinicDataService = new ClinicDataService(webDriver);
        ClinicianDataService clinicianDataService = new ClinicianDataService(webDriver);

        for (int i = 0; i < TestConfig.numOfClinics; i++) {
            TestClinic clinic = TestClinic.withMandatory().name("clinic" + i);
            TestClinician testClinician = TestClinician.withMandatory().name("clinician" + i).userName("clinician" + i).clinic(clinic);

            clinicDataService.create(clinic);
            clinicianDataService.create(testClinician);
        }
    }

}

