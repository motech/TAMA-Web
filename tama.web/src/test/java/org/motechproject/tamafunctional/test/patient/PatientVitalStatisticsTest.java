package org.motechproject.tamafunctional.test.patient;

import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import static junit.framework.Assert.assertEquals;

public class PatientVitalStatisticsTest extends BaseTest {

    private TestClinician clinician;

    @Override
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void enterVitalStatisticsForActivePatient() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        patientDataService.setInitialVitalStatistics(vitalStatistics, patient, clinician);

        TestVitalStatistics savedVitalStatistics = patientDataService.getInitialVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);

        vitalStatistics.heightInCm(new Double(155));
        patientDataService.updateVitalStatistics(vitalStatistics, patient, clinician);

        savedVitalStatistics = patientDataService.getInitialVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);
    }
}
