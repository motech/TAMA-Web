package org.motechproject.tamaregression.ivr;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.*;
import org.motechproject.tamafunctionalframework.testdata.*;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class SymptomReportingTreeTest extends BaseIVRTest {

    private TestClinician clinician;
    private TestPatient patient;
    private XStream xStream = new XStream();

    @Test
    public void shouldTakeThePatientToTheCorrectSymptomReportingTreeAndCreateAlert() throws IOException {
        setupDataForSymptomReporting();
        assertSymptomReportingCallFlow(patient);

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

        patient = TestPatient.withMandatory().patientPreferences(TestPatientPreferences.withMandatory().passcode("5678"));
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(patient, clinician);
        patientDataService.createRegimen(patient, clinician, treatmentAdvice, labResult, TestVitalStatistics.withMandatory());
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
        alertsPage.assertTableContainsAlert(patient.patientId(), patient.mobileNumber(), "Open", "");
    }

    private ShowAlertPage updateNotesAndCloseAlert(TestPatient patient, String status, String notes, AlertsPage alertsPage) {
        UpdateAlertPage updateAlertPage = alertsPage.openUpdateAlertPage(patient.patientId());
        updateAlertPage.changeSymptomReportingAlertStatus(status);
        updateAlertPage.changeNotes(notes);
        return updateAlertPage.save();
    }

    private void assertSymptomReportingCallFlow(TestPatient patient) throws IOException {
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
