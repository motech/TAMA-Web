package org.motechproject.tamasmoke;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowAlertPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.testdata.*;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class DialClinicianTest extends BaseIVRTest {
    private TestClinician clinician;
    private TestPatient patient;
    private XStream xStream = new XStream();

    @Test
    public void shouldDialClinicianContacts_InCertain_SymptomReportedCallFlows_AndAClinicianRespondsToTheCall() throws IOException {
        setupDataForSymptomReporting();
        caller = caller(patient);
        patientCallsTAMA_AndListensToPillMenu();
        patientReportsSymptoms();
        clinician0AnswersTheCall();
        caller.hangup();
        verifyAlertRaised(clinician.clinicContactName0());
        verifyPatientSuspended();
        patientCallsTAMA_AndVerifyPillMenuNotPlayed();
        caller.hangup();
    }

    @Test
    public void shouldDialClinicianContacts_InCertain_SymptomReportedCallFlows_AndNoClinicianRespondsToTheCall() throws IOException {
        setupDataForSymptomReporting();
        caller = caller(patient);
        patientCallsTAMA_AndListensToPillMenu();
        patientReportsSymptoms();
        noClinicianAnswersTheCall();
        caller.hangup();
        verifyAlertRaised("No");
        verifyPatientSuspended();
        patientCallsTAMA_AndVerifyPillMenuNotPlayed();
        caller.hangup();
    }

    private void setupDataForSymptomReporting() {
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        patient = TestPatient.withMandatory().patientPreferences(TestPatientPreferences.withMandatory().passcode("5678"));
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Combivir", "Efferven"));
        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(treatmentAdvice, labResult, TestVitalStatistics.withMandatory(), patient, clinician);
    }

    private void patientCallsTAMA_AndListensToPillMenu() {
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        logInfo("****************************************************************************************************");
        logInfo(xStream.toXML(ivrResponse));
        logInfo("****************************************************************************************************");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, "greeting2generic", TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, "pillazt3tc_combivir", "pillefv_efferven", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, TamaIVRMessage.DOSE_TAKEN_MENU_OPTION, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
    }

    private void patientCallsTAMA_AndVerifyPillMenuNotPlayed() {
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
    }

    private void patientReportsSymptoms() {
        // Regimen4_2
        IVRResponse ivrResponse = caller.enter("2");
        IVRAssert.assertAudioFilesPresent(ivrResponse, TamaIVRMessage.START_SYMPTOM_FLOW);

        caller.listenMore();
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "q_nauseaorvomiting");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cy_nauseaorvomiting", "q_shortnessofbreathorgiddiness");

        ivrResponse = caller.enter("3");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cn_shortnessofbreathorgiddiness", "q_palpitationorfatigue");

        ivrResponse = caller.enter("3");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cn_palpitationorfatigue", "q_fever");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cy_fever", "q_headache");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "ppc_nvfevhead", "adv_crocin01");
    }

    private void clinician0AnswersTheCall() {
        IVRResponse ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber0());

        caller.answered();
    }

    private void noClinicianAnswersTheCall() {
        IVRResponse ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber0());

        ivrResponse = caller.notAnswered();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber1());

        ivrResponse = caller.notAnswered();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber2());

        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cannotcontact01");
    }

    private void verifyAlertRaised(String clinicianName) {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        ShowAlertPage showAlertsPage = listPatientsPage.goToAlertsPage().filterUnreadAlerts().openShowAlertPage(patient.patientId());
        assertEquals(clinicianName, showAlertsPage.connectedToDoctor());
        showAlertsPage.logout();
    }

    private void verifyPatientSuspended() {
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient);
        assertEquals("Suspended adherence calls", showPatientPage.getStatus().trim());
        showPatientPage.logout();
    }
}
