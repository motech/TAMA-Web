package org.motechproject.tamafunctional.test.ivr;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.*;
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
        AlertsPage alertsPage = listPatientsPage.goToAlertsPage().filterUnreadAlerts();

        assertAlertIsCreated(patient, alertsPage);

        String notes = "some notes";
        String status = "Closed";

        ShowAlertPage showAlertPage = updateNotesAndCloseAlert(patient, status, notes, alertsPage);
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
        AlertsPage readAlertsPage = listPatientsPage.goToAlertsPage().filterReadAlerts();
        readAlertsPage.assertTableContainsAlert(patient.patientId(), patient.mobileNumber(), status, notes);
    }

    private void assertAlertIsCreated(TestPatient patient, AlertsPage alertsPage) {
        List<WebElement> webElements = alertsPage.alertsTable();
        alertsPage.assertTableContainsAlert(patient.patientId(), patient.mobileNumber(), "Open", "");
    }

    private ShowAlertPage updateNotesAndCloseAlert(TestPatient patient, String status, String notes, AlertsPage alertsPage) {
        UpdateAlertPage updateAlertPage = alertsPage.openUpdateAlertPage(patient.patientId());
        updateAlertPage.changeSymptomReportingAlertStatus(status);
        updateAlertPage.changeNotes(notes);
        return updateAlertPage.save();
    }

   private void assertSymptomReportingCallFlow(TestClinician clinician, TestPatient patient) throws IOException {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        logInfo("****************************************************************************************************");
        logInfo(xStream.toXML(ivrResponse));
        logInfo("****************************************************************************************************");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, "greeting2generic", TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, TamaIVRMessage.DOSE_TAKEN_MENU_OPTION, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);

        // Regimen4_2
        caller.enter("2");
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "q_nauseaorvomiting");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cy_nauseaorvomiting", "q_shortnessofbreathorgiddiness");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "ppc_nvshortbreathgiddi", "adv_continuemedicineseeclinicasap");

        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, TamaIVRMessage.HANGUP_OR_MAIN_MENU);

        caller.hangup();
    }
}
