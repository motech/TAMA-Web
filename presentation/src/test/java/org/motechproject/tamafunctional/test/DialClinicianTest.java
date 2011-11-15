package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.Status;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import java.io.IOException;

public class DialClinicianTest extends BaseIVRTest {
    private TestClinician clinician;
    private TestPatient patient;

    @Override
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinc(clinician);
        patient = TestPatient.withMandatory();
        new PatientDataService(webDriver).createTestPatientForSymptomReporting(patient, clinician);
    }

    @Test
    public void shouldDialClinicianContacts_InCertain_SymptomReportedCallFlows() throws IOException {
        caller = caller(patient);
        patientCallsTAMA_AndListensToPillMenu();
        patientReportsSymptoms();
        dialClinicianContacts();
        caller.hangup();
        verifyPatientSuspended();
        patientCallsTAMA_AndVerifyPillMenuNotPlayed();
        caller.hangup();
    }

    private void patientCallsTAMA_AndListensToPillMenu() {
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        asksForCollectDtmfWith(ivrResponse, "welcome_to_" + clinician.clinic().name(), TamaIVRMessage.ITS_TIME_FOR_THE_PILL, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.PILL_FROM_THE_BOTTLE, TamaIVRMessage.PILL_CONFIRM_CALL_MENU);
    }

    private void patientCallsTAMA_AndVerifyPillMenuNotPlayed() {
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.MENU_010_05_01_MAINMENU4);
    }

    private void patientReportsSymptoms() {
        // Regimen4_2
        caller.enter("2");
        caller.enter("2");
        IVRResponse ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, "q_nauseaorvomiting");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "cy_nauseaorvomiting", "q_shortnessofbreathorgiddiness");

        ivrResponse = caller.enter("3");
        assertAudioFilesPresent(ivrResponse, "cn_shortnessofbreathorgiddiness", "q_palpitationorfatigue");

        ivrResponse = caller.enter("3");
        assertAudioFilesPresent(ivrResponse, "cn_palpitationorfatigue", "q_fever");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "cy_fever", "q_headache");

        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, "ppc_nvfevhead", "adv_crocin01");
    }

    private void dialClinicianContacts() {
        IVRResponse ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber0());

        ivrResponse = caller.notAnswered();
        assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber1());

        ivrResponse = caller.notAnswered();
        assertAudioFilesPresent(ivrResponse, "connectingdr");
        assertClinicianPhoneNumberPresent(ivrResponse, clinician.clinicContactNumber2());

        ivrResponse = caller.listenMore();
        assertAudioFilesPresent(ivrResponse, "cannotcontact01");
    }

    private void verifyPatientSuspended() {
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient);
        Assert.assertEquals(showPatientPage.getStatus().trim(), Status.Suspended.toString());
    }
}
