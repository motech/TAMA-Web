package org.motechproject.tamaregression.symptoms;

import com.thoughtworks.xstream.XStream;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.AlertsPage;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.testdata.*;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class RegimenOneTest extends BaseIVRTest {

    private TestClinician clinician;
    private TestPatient patient;
    private PatientDataService patientDataService;
    private LocalDate today;
    private XStream xStream = new XStream();

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
        today = DateUtil.today();
    }

    @Test
    public void verifySymptomsCallFlow_WhenPatientIs_50_yrsOld_withCD4CountOf_50() throws IOException {
        create_50_yrsOldPatientWithRegimenOne();
        assertSymptomReportingCallFlow(patient);
        assertSymptomReportingAlertRaised();
    }

    @Test
    public void verifyPatientIsTakenToSymptomReportingCallFlow_WhenTAMACallsPatient_AndPatientSaysCannotTakeDose(){
        create_50_yrsOldPatientWithRegimenOne();
        assertTransitionToSymptomReportingCallFlow();
    }

    private void assertTransitionToSymptomReportingCallFlow() {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.replyToCall(new PillReminderCallInfo(1));
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, DEFAULT_OUTBOUND_CLINIC_MESSAGE, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE, PILL_REMINDER_RESPONSE_MENU);

        ivrResponse = caller.enter("3");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, MISSED_PILL_FEEDBACK_FIRST_TIME, DOSE_CANNOT_BE_TAKEN_MENU);

        ivrResponse = caller.enter("1");
        caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, TamaIVRMessage.START_SYMPTOM_FLOW, "q_fever");
    }

    private void assertSymptomReportingCallFlow(TestPatient patient) throws IOException {
        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("5678#");
        logInfo("****************************************************************************************************");
        logInfo(xStream.toXML(ivrResponse));
        logInfo("****************************************************************************************************");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, "greeting2generic", "pilltdf3tc_tavin-l", "pillnvp_nevir", TamaIVRMessage.DOSE_TAKEN_MENU_OPTION, TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);

        // Regimen1
        ivrResponse = caller.enter("2");

        caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, TamaIVRMessage.START_SYMPTOM_FLOW, "q_fever");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "cy_fever", "q_headachevomiting");

        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, "ppc_fevheadvom", "adv_crocin01");

        ivrResponse = ignoreContactingDoctor(5);
        IVRAssert.assertAudioFilesPresent(ivrResponse, "001_06_06_mayhangupormainmenu");

        ivrResponse = caller.listenMore();

        caller.hangup();
    }

    private void assertSymptomReportingAlertRaised() {
        AlertsPage alertsPage = gotoAlertsPage();
        assertTrue(alertsPage.hasAlertOfType(PatientAlertType.SymptomReporting.toString()));
    }

    private AlertsPage gotoAlertsPage() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        return listPatientsPage.goToAlertsPage().filterUnreadAlerts();
    }

    private IVRResponse ignoreContactingDoctor(int times) {
        IVRResponse ivrResponse = null;
        for (int i = 0; i < times; i++) {
            ivrResponse = caller.listenMore();
        }
        return ivrResponse;
    }

    private void create_50_yrsOldPatientWithRegimenOne() {
        createPatient(51);

        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        TestLabResult labResult = TestLabResult.withMandatory().setCd4Count("23/12/2011", "50");
        TestTreatmentAdvice treatmentAdvice = createRegimenOne();

        patientDataService.registerAndActivate(treatmentAdvice, labResult, vitalStatistics, patient, clinician);
    }

    private void createPatient(int age) {
        patient = TestPatient.withMandatory().dateOfBirth(today.minusYears(age)).patientPreferences(TestPatientPreferences.withMandatory().passcode("5678"));
        patientDataService = new PatientDataService(webDriver);
    }

    private TestTreatmentAdvice createRegimenOne() {
        LocalDate regimenStartDate = today.minusMonths(7);
        LocalTime doseTime = new LocalTime(10, 0, 0);
        TestDrugDosage[] drugDosages = TestDrugDosage.create(regimenStartDate, doseTime, "Tavin-L", "Nevir");
        return TestTreatmentAdvice.withExtrinsic(drugDosages).regimenName("TDF + 3TC / fTC + NVP").drugCompositionName("TDF+3TC+NVP");
    }
}
