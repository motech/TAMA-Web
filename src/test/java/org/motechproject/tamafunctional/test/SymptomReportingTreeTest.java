package org.motechproject.tamafunctional.test;

import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.UnreadAlertsPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class SymptomReportingTreeTest extends BaseIVRTest {
    @Test
    public void shouldTakeThePatientToTheCorrectSymptomReportingTreeAndCreateAlert() throws IOException {
        TestClinician clinician = createTestClinician();
        TestPatient patient = createTestPatient(clinician);

        assertSymptomReportingCallFlow(clinician, patient);
        assertAlertIsCreated(clinician, patient);

        //readAndCloseAlert(clinician, patient);
    }

    private void readAndCloseAlert(TestClinician clinician, TestPatient patient) {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        UnreadAlertsPage unreadAlertsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(),clinician.password()).goToUnreadAlertsPage();
        List<WebElement> webElements = unreadAlertsPage.alertsTable();
        openAlert(webElements, patient.patientId());
    }

    private void openAlert(List<WebElement> webElements, String patientId) {
        for(WebElement trElement : webElements)
        {
            List<WebElement> td_collection=trElement.findElements(By.xpath("td"));
            String actualPatientId = td_collection.get(0).getText();
            if(patientId.equals(actualPatientId)) {
                //open
                List<WebElement> elementsWithLinks = trElement.findElements(By.xpath("td/a"));
                elementsWithLinks.get(1).click();
                MyPageFactory.initElements(webDriver, UnreadAlertsPage.class);
                break;
            }
        }
    }

    private void assertAlertIsCreated(TestClinician clinician, TestPatient patient) {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        UnreadAlertsPage unreadAlertsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(),clinician.password()).goToUnreadAlertsPage();
        List<WebElement> webElements = unreadAlertsPage.alertsTable();
        assertTableContainsAlert(webElements, patient.patientId(), patient.mobileNumber());
    }

    private TestPatient createTestPatient(TestClinician clinician) {
        TestPatient patient = TestPatient.withMandatory();
        patient.patientPreferences().passcode("5678");

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);

        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));
        patientDataService.setupLabResult(patient, clinician, labResult);

        patientDataService.setInitialVitalStatistics(TestVitalStatistics.withMandatory(), patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        patientDataService.createARTRegimen(treatmentAdvice, patient, clinician);

        TestTreatmentAdvice savedTreatmentAdvice = patientDataService.getTreatmentAdvice(patient, clinician);
        assertEquals(savedTreatmentAdvice.regimenName(), treatmentAdvice.regimenName());
        assertEquals(savedTreatmentAdvice.drugCompositionName(), treatmentAdvice.drugCompositionName());
        return patient;
    }

    private TestClinician createTestClinician() {
        TestClinician clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinc(clinician);
        return clinician;
    }

    private void assertTableContainsAlert(List<WebElement> webElements, String patientId, String phoneNumber) {
        for(WebElement trElement : webElements)
        {
            List<WebElement> td_collection=trElement.findElements(By.xpath("td"));
            String actualPatientId = td_collection.get(0).getText();
            if(patientId.equals(actualPatientId)) {
                String actualPhoneNumber = td_collection.get(1).getText();
                assertEquals(phoneNumber, actualPhoneNumber);
                return;
            }
        }
        fail();
    }

    private void assertSymptomReportingCallFlow(TestClinician clinician, TestPatient patient) throws IOException {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        asksForCollectDtmfWith(ivrResponse, "welcome_to_" + clinician.clinic().name(), TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.PILL_FROM_THE_BOTTLE, TamaIVRMessage.PILL_CONFIRM_CALL_MENU);

        // Regimen4_2
        ivrResponse = caller.enter("2");
        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, "q_nauseaorvomiting");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "cy_nauseaorvomiting", "q_shortnessofbreathorgiddiness");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "ppc_nvshortbreathgiddi", "adv_continuemedicineseeclinicasap");

        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, TamaIVRMessage.MORE_OPTIONS, TamaIVRMessage.SIGNATURE_MUSIC);

        caller.hangup();
    }


}
