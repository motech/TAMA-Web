package org.motechproject.tamaregression.pillreminder;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.dailypillreminder.listener.AdherenceQualityListener;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.ScheduledTaskManager;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowAlertPage;
import org.motechproject.tamafunctionalframework.page.UpdateAlertPage;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class AdherenceInRedTest extends BaseIVRTest {
    private TestPatient patient;
    private TestClinician clinician;
    private ScheduledTaskManager scheduledTaskManager;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Combivir", "Efferven");
        LocalDate yesterday = DateUtil.today().minusDays(1);
        drugDosages[0].startDate(yesterday);
        drugDosages[1].startDate(yesterday);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
        scheduledTaskManager = new ScheduledTaskManager(webClient);
        caller = caller(patient);
    }

    @Test
    public void shouldRaise_RedAlert_WhenAdherenceFalls_Below70Percent() throws IOException {
        caller.replyToCall(new PillReminderCallInfo(1));
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE, PILL_REMINDER_RESPONSE_MENU);
        ivrResponse = caller.enter("3");
        caller.hangup();
        triggerRedAlertJob();
        verifyCreationOfRedAlertForThePatient();
    }

    private void triggerRedAlertJob() {
        scheduledTaskManager.trigger(AdherenceQualityListener.class, "determineAdherenceQualityAndRaiseAlert", patient.id());
    }

    private void verifyCreationOfRedAlertForThePatient() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        UpdateAlertPage updateAlertPage = listPatientsPage.goToAlertsPage().filterUnreadAlerts().openUpdateAlertPage(patient.patientId());
        updateAlertPage.changeNotes("testnotes");
        ShowAlertPage showAlertsPage = updateAlertPage.save();
        assertEquals(patient.patientId(), showAlertsPage.patientId());
        assertEquals("AdherenceInRed", showAlertsPage.alertType());
        assertEquals("Adherence percentage is 0.00%", showAlertsPage.description());
        assertEquals("Daily", showAlertsPage.callPreference());
        showAlertsPage.logout();
    }
}
