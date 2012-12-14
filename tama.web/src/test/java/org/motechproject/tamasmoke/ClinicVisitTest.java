package org.motechproject.tamasmoke;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowClinicVisitListPage;
import org.motechproject.tamafunctionalframework.testdata.*;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class ClinicVisitTest extends BaseTest {

    private TestClinician clinician;
    private TestPatient patient;
    private PatientDataService patientDataService;
    private TestTreatmentAdvice treatmentAdvice;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
        patient = TestPatient.withMandatory();
        patientDataService = new PatientDataService(webDriver);
        treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Combivir", "Efferven"));
    }

    @Test
    public void testCreateRegimenWithLabResults_ForTheFirstClinicVisit() {
        TestLabResult labResult = TestLabResult.withMandatory();
        patientDataService.registerAndActivate(treatmentAdvice, labResult, patient, clinician);
        ShowClinicVisitListPage showClinicVisitListPage = gotoShowClinicVisitPage();

        assertEquals("Activated in TAMA", showClinicVisitListPage.getFirstVisitDescription());
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
        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        patientDataService.registerAndActivate(treatmentAdvice, vitalStatistics, patient, clinician);
        ShowClinicVisitListPage showClinicVisitListPage = gotoShowClinicVisitPage();

        assertEquals("Activated in TAMA", showClinicVisitListPage.getFirstVisitDescription());
        showClinicVisitListPage.logout();

        TestVitalStatistics savedVitalStatistics = patientDataService.getSavedVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);

        vitalStatistics.heightInCm((double) 155);
        patientDataService.updateVitalStatistics(patient, clinician, vitalStatistics);

        savedVitalStatistics = patientDataService.getSavedVitalStatistics(patient, clinician);
        assertEquals(vitalStatistics, savedVitalStatistics);
    }

    @Test
    public void testCreateRegimenWithOpportunisticInfections_ForTheFirstClinicVisit() {
        TestOpportunisticInfections opportunisticInfections = TestOpportunisticInfections.withMandatory();
        patientDataService.registerAndActivate(treatmentAdvice, opportunisticInfections, patient, clinician);

        TestOpportunisticInfections savedOpportunisticInfections = patientDataService.getSavedOpportunisticInfections(patient, clinician);
        assertEquals(opportunisticInfections, savedOpportunisticInfections);

        opportunisticInfections.setOther(false);
        opportunisticInfections.setOtherDetails("");
        patientDataService.updateOpportunisticInfections(patient, clinician, opportunisticInfections);

        savedOpportunisticInfections = patientDataService.getSavedOpportunisticInfections(patient, clinician);
        assertEquals(opportunisticInfections, savedOpportunisticInfections);
    }

    @Test
    public void testAdjustDueDateAsToday(){
        ShowClinicVisitListPage showClinicVisitListPage = activatePatientAndGotoShowClinicVisitsPage();

        showClinicVisitListPage.adjustDueDateAsToday();

        String today = DateUtil.today().toString(TAMAConstants.DATE_FORMAT);
        assertEquals(today, showClinicVisitListPage.getAdjustedDueDate());
    }

    @Test
    public void testMarkAsMissed(){
        ShowClinicVisitListPage showClinicVisitListPage = activatePatientAndGotoShowClinicVisitsPage();

        showClinicVisitListPage.markAsMissed();

        assertEquals("Missed", showClinicVisitListPage.getVisitDate());
    }

    private ShowClinicVisitListPage activatePatientAndGotoShowClinicVisitsPage() {
        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        TestLabResult labResult = TestLabResult.withMandatory();

        patientDataService.registerAndActivate(treatmentAdvice, labResult, vitalStatistics, patient, clinician);

        return gotoShowClinicVisitPage();
    }

    private ShowClinicVisitListPage gotoShowClinicVisitPage() {
        return MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient).goToClinicVisitListPage();
    }
}
