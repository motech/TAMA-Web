package org.motechproject.tamafunctional.test;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowAlertPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import java.io.IOException;

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
        patient = TestPatient.withMandatory();
        new PatientDataService(webDriver).createTestPatientForSymptomReporting(patient, clinician);
    }

    private void patientCallsTAMA_AndListensToPillMenu() {
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        logInfo("****************************************************************************************************");
        logInfo(xStream.toXML(ivrResponse));
        logInfo("****************************************************************************************************");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, "greeting2generic", TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.FROM_THE_BOTTLE, TamaIVRMessage.DOSE_TAKEN_MENU_OPTION, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
    }

    private void patientCallsTAMA_AndVerifyPillMenuNotPlayed() {
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
    }

    private void patientReportsSymptoms() {
        // Regimen4_2
        caller.enter("2");
        IVRResponse ivrResponse = caller.listenMore();
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
