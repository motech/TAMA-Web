package org.motechproject.tamaregression.serial;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.clinicvisits.handler.AppointmentReminderHandler;
import org.motechproject.tama.clinicvisits.handler.VisitReminderHandler;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.framework.ScheduledTaskManager;
import org.motechproject.tamafunctionalframework.page.AlertsPage;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowClinicVisitListPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;

import static junit.framework.Assert.assertTrue;

public class ClinicVisitRegressionTest extends BaseTest {

    private TestClinician clinician;
    private TAMADateTimeService tamaDateTimeService;
    private MyWebClient webclient;
    private TestPatient patient;
    private PatientDataService patientDataService;
    private ScheduledTaskManager scheduledTaskManager;
    private DateTime now;


    @Before
    public void setUp() {
        super.setUp();
        now = DateUtil.now();
        clinician = TestClinician.withMandatory();
        webclient = new MyWebClient();
        tamaDateTimeService = new TAMADateTimeService(webclient);
        scheduledTaskManager = new ScheduledTaskManager(webclient);
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        patient = TestPatient.withMandatory();
        patientDataService = new PatientDataService(webDriver);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        tamaDateTimeService.adjustDateTime(now);
    }

    @Test
    public void testLostAppointmentAlert_WhenAppointmentIsNotConfirmedBeforeDueDate() throws ParseException {
        ShowClinicVisitListPage showClinicVisitListPage = gotoShowClinicVisitsPage();
        DateTime appointmentDate = showClinicVisitListPage.getDueDate();
        showClinicVisitListPage.logout();

        tamaDateTimeService.adjustDateTime(appointmentDate.plusDays(2));
        String jobId = patient.id() + "week4" + "0";
        scheduledTaskManager.trigger(AppointmentReminderHandler.class, "handleEvent", jobId);


        AlertsPage alertsPage = gotoAlertsListPage();
        assertTrue(alertsPage.hasAlertOfType(PatientAlertType.AppointmentConfirmationMissed.toString()));
    }

    @Test
    public void testMissedVisitAlert_WhenPatientMissedHisConfirmedVisitDate() throws ParseException {
        ShowClinicVisitListPage showClinicVisitListPage = gotoShowClinicVisitsPage();
        showClinicVisitListPage.scheduleConfirmVisitDateAsToday();
        DateTime confirmVisitDate = showClinicVisitListPage.getConfirmVisitDate();

        showClinicVisitListPage.logout();

        tamaDateTimeService.adjustDateTime(confirmVisitDate.plusDays(2));
        String jobId = patient.id() + "week4";
        scheduledTaskManager.trigger(VisitReminderHandler.class, "handleEvent", jobId);


        AlertsPage alertsPage = gotoAlertsListPage();
        assertTrue(alertsPage.hasAlertOfType(PatientAlertType.VisitMissed.toString()));
    }


    private ShowClinicVisitListPage gotoShowClinicVisitsPage() {
        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        TestLabResult labResult = TestLabResult.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));

        patientDataService.registerAndActivate(treatmentAdvice, labResult, vitalStatistics, patient, clinician);

        return MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient).goToClinicVisitListPage();
    }

    private AlertsPage gotoAlertsListPage() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        return listPatientsPage.goToAlertsPage().filterUnreadAlerts();
    }
}
