package org.motechproject.tamasmoke;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowClinicVisitListPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class ClinicVisitTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void testCreateRegimenWithLabResults_ForTheFirstClinicVisit() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        TestLabResult labResult = TestLabResult.withMandatory();

        patientDataService.registerAndActivate(treatmentAdvice, labResult, patient, clinician);

        ShowClinicVisitListPage showClinicVisitListPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient).goToClinicVisitListPage();
        assertEquals("Registered with TAMA", showClinicVisitListPage.getFirstVisitDescription());
        showClinicVisitListPage.logout();

        TestLabResult savedLabResult = patientDataService.getSavedLabResult(patient, clinician);
        assertEquals(labResult, savedLabResult);

        final TestLabResult newLabResults = TestLabResult.withMandatory().results(Arrays.asList("1", "2"));
        patientDataService.updateLabResults(patient, clinician, newLabResults);

        final TestLabResult updatedLabResult = patientDataService.getSavedLabResult(patient, clinician);
        assertEquals(newLabResults, updatedLabResult);
    }

    @Test
    public void testCreateRegimenWithVitalStatistics_ForTheFirstClinicVisit() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));

        patientDataService.registerAndActivate(treatmentAdvice, vitalStatistics, patient, clinician);

        ShowClinicVisitListPage showClinicVisitListPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient).goToClinicVisitListPage();
        assertEquals("Registered with TAMA", showClinicVisitListPage.getFirstVisitDescription());
        showClinicVisitListPage.logout();

        TestVitalStatistics savedVitalStatistics = patientDataService.getSavedVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);

        vitalStatistics.heightInCm((double) 155);
        patientDataService.updateVitalStatistics(patient, clinician, vitalStatistics);

        savedVitalStatistics = patientDataService.getSavedVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);
    }
}
