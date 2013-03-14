package org.motechproject.tamasmoke;


import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientReportsPage;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertTrue;

public class DailyPillReminderReportsTest extends BaseIVRTest {
    private TestPatient patient;
    private TestClinician clinician;
    LocalDate regimenStartDate;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Combivir", "Efferven");
        regimenStartDate = DateUtil.today().minusDays(1);
        drugDosages[0].startDate(regimenStartDate);
        drugDosages[1].startDate(regimenStartDate);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    @Test
    public void shouldFetchAllDosageAdherenceLogsForThePatient(){
        recordDoseAsTaken();
        ShowPatientReportsPage showPatientReportsPage = goToPatientReportsPage();

        showPatientReportsPage.generateDailyPillReminderReport(DateUtil.today(), DateUtil.tomorrow());

        String dailyPillReminderReportText = showPatientReportsPage.getDailyPillReminderReportText();
        assertTrue(dailyPillReminderReportText.contains("TAKEN"));
        assertTrue(dailyPillReminderReportText.contains(DateUtil.today().toString()));
    }

    private ShowPatientReportsPage goToPatientReportsPage() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        ShowPatientPage showPatientPage = listPatientsPage.gotoShowPatientPage(patient);

        return showPatientPage.goToPatientReportsPage();
    }

    private void recordDoseAsTaken() {
        caller.replyToCall(new PillReminderCallInfo(1));
        caller.enter(patient.patientPreferences().passcode());
        caller.enter("1");
        caller.hangup();
    }
}