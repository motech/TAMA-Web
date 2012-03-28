package org.motechproject.tamaregression.serial;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ReactivatePatientPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.testdata.*;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class BackfillAdherenceTest extends BaseIVRTest {
    private TestPatient patient;
    private TestClinician clinician;
    private TAMADateTimeService tamaDateTimeService;
    private PatientDataService patientDataService;

    @Before
    public void setUp() {
        super.setUp();
        tamaDateTimeService = new TAMADateTimeService(webClient);
        tamaDateTimeService.adjustDateTime(DateUtil.now().minusWeeks(2));
        setupData();
    }

    @After
    public void tearDown() throws IOException {
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        super.tearDown();
    }

    @Test
    public void shouldSuspendPatient_BackfillAdherence_AndVerifyCorrectAdherencePercentage() throws IOException {
        suspendPatientThroughSymptomsFlow();

        tamaDateTimeService.adjustDateTime(DateUtil.now().minusDays(1));
        verifyPatientSuspended();

        ReactivatePatientPage reactivatePatientPage = patientDataService.reactivatePatient(patient, clinician);
        reactivatePatientPage.backfillAdherenceAsTakenByDefault();

        tamaDateTimeService.adjustDateTime(DateUtil.now());

        verifyPatientActivatedAndAdherenceLogsBackfilled_ByCheckingCorrectAdherencePercentage();
    }

    private void verifyPatientActivatedAndAdherenceLogsBackfilled_ByCheckingCorrectAdherencePercentage() {
        caller = caller(patient);
        caller.replyToCall(new PillReminderCallInfo(1));
        IVRResponse ivrResponse = caller.enter("5678");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, DEFAULT_OUTBOUND_CLINIC_MESSAGE, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE, PILL_REMINDER_RESPONSE_MENU);
        ivrResponse = caller.enter("3");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, MISSED_PILL_FEEDBACK_FIRST_TIME, DOSE_CANNOT_BE_TAKEN_MENU);
        ivrResponse = caller.enter("3");
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS_NOW, getNumberFilename(93), PERCENT);
    }

    private void suspendPatientThroughSymptomsFlow() {
        caller.call();
        IVRResponse ivrResponse = caller.enter("5678");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, DEFAULT_INBOUND_CLINIC_MESSAGE, ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW);

        ivrResponse = caller.enter("2");

        caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, TamaIVRMessage.START_SYMPTOM_FLOW, "q_nauseaorvomiting");

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

        ivrResponse = caller.listenMore();
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
        caller.hangup();
    }

    private void setupData() {
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        patient = TestPatient.withMandatory().patientPreferences(TestPatientPreferences.withMandatory().passcode("5678"));
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Combivir", "Efferven");
        LocalDate today = DateUtil.today().minusWeeks(2);
        drugDosages[0].startDate(today);
        drugDosages[1].startDate(today);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);
        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));

        patientDataService = new PatientDataService(webDriver);
        patientDataService.registerAndActivate(treatmentAdvice, labResult, TestVitalStatistics.withMandatory(), patient, clinician);
        caller = caller(patient);
    }

    private void verifyPatientSuspended() {
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient);
        assertEquals("Suspended adherence calls", showPatientPage.getStatus().trim());
        showPatientPage.logout();
    }

}
