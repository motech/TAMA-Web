package org.motechproject.tamafunctional.test;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.*;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class SymptomReportingTreeTest extends BaseIVRTest {

    private TestClinician clinician;
    private TestPatient patient;
    private XStream xStream = new XStream();

    @Test
    public void shouldTakeThePatientToTheCorrectSymptomReportingTreeAndCreateAlert() throws IOException {
        setupDataForSymptomReporting();
        assertSymptomReportingCallFlow(clinician, patient);

        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        UnreadAlertsPage unreadAlertsPage = listPatientsPage.goToUnreadAlertsPage();

        assertAlertIsCreated(patient, unreadAlertsPage);

        String notes = "some notes";
        String status = "Closed";

        ShowAlertPage showAlertPage = updateNotesAndCloseAlert(patient, status, notes, unreadAlertsPage);
        assertShowAlert(notes, status, showAlertPage);

        assertAlertIsUpdated(patient, listPatientsPage, status, notes);
    }

    private void setupDataForSymptomReporting() {
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
        patient = TestPatient.withMandatory();
        new PatientDataService(webDriver).createTestPatientForSymptomReporting(patient, clinician);
    }

    private void assertShowAlert(String notes, String status, ShowAlertPage showAlertPage) {
        assertEquals(status, showAlertPage.alertStatus());
        assertEquals(notes, showAlertPage.notes());
    }

    private void assertAlertIsUpdated(TestPatient patient, ListPatientsPage listPatientsPage, String status, String notes) {
        ReadAlertsPage readAlertsPage = listPatientsPage.goToReadAlertsPage();
        assertTableContainsAlert(readAlertsPage.alertsTable(), patient.patientId(), patient.mobileNumber(), status, notes);
    }

    private void assertAlertIsCreated(TestPatient patient, UnreadAlertsPage unreadAlertsPage) {
        List<WebElement> webElements = unreadAlertsPage.alertsTable();
        assertTableContainsAlert(webElements, patient.patientId(), patient.mobileNumber(), "Open", "");
    }

    private ShowAlertPage updateNotesAndCloseAlert(TestPatient patient, String status, String notes, UnreadAlertsPage unreadAlertsPage) {
        UpdateAlertPage updateAlertPage = unreadAlertsPage.openUpdateAlertPage(patient.patientId());
        updateAlertPage.changeSymptomReportingAlertStatus(status);
        updateAlertPage.changeNotes(notes);
        return updateAlertPage.save();
    }

    private int getRowId(List<WebElement> webElements, String patientId) {
        int rowId = 0;
        for (WebElement trElement : webElements) {
            List<WebElement> td_collection = trElement.findElements(By.xpath("td"));
            String actualPatientId = td_collection.get(0).getText();
            if (patientId.equals(actualPatientId)) {
                return rowId;
            }
            rowId++;
        }
        return -1;
    }

    private void assertTableContainsAlert(List<WebElement> webElements, String patientId, String phoneNumber, String status, String notes) {
        int rowId = getRowId(webElements, patientId);
        assertTrue(rowId >= 0);
        WebElement trElement = webElements.get(rowId);
        List<WebElement> td_collection = trElement.findElements(By.xpath("td"));
        String actualPhoneNumber = td_collection.get(1).getText();
        assertEquals(phoneNumber, actualPhoneNumber);
        assertEquals(status, td_collection.get(5).getText());
        assertEquals(notes, td_collection.get(6).getText());
    }

    private void assertSymptomReportingCallFlow(TestClinician clinician, TestPatient patient) throws IOException {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        logInfo("****************************************************************************************************");
        logInfo(xStream.toXML(ivrResponse));
        logInfo("****************************************************************************************************");
        asksForCollectDtmfWith(ivrResponse, "welcome_to_" + clinician.clinic().name(), TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.PILL_FROM_THE_BOTTLE, TamaIVRMessage.DOSE_TAKEN_MENU_OPTION, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);

        // Regimen4_2
        caller.enter("2");
        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, "q_nauseaorvomiting");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "cy_nauseaorvomiting", "q_shortnessofbreathorgiddiness");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "ppc_nvshortbreathgiddi", "adv_continuemedicineseeclinicasap");

        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, TamaIVRMessage.HANGUP_OR_MAIN_MENU);

        caller.hangup();
    }
}
