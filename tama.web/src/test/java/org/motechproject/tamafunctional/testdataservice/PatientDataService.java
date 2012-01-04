package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.*;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;

public class PatientDataService extends EntityDataService {
    public PatientDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void registerAndActivate(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = registerWithoutLogout(patient, clinician);
        showPatientPage.activatePatient().logout();
    }

    private ShowPatientPage registerWithoutLogout(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = login(clinician).
                goToPatientRegistrationPage().
                registerNewPatient(patient);
        patient.id(showPatientPage.patientDocId());
        logInfo("{Created}{Patient}{DocId=%s}", patient.id());
        return showPatientPage;
    }

    public void register(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = registerWithoutLogout(patient, clinician);
        showPatientPage.logout();
    }

    private ListPatientsPage login(TestClinician clinician) {
        return MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
    }

    private ShowPatientPage viewPatient(TestPatient patient, TestClinician clinician) {
        return login(clinician).gotoShowPatientPage(patient);
    }

    public void createARTRegimen(TestTreatmentAdvice treatmentAdvice, TestPatient patient, TestClinician clinician) {
        viewPatient(patient, clinician).goToCreateARTRegimenPage().registerNewARTRegimen(treatmentAdvice).logout();
    }

    public void reCreateARTRegimen(TestTreatmentAdvice treatmentAdvice, TestPatient patient, TestClinician clinician) {
        viewPatient(patient, clinician).goToCreateARTRegimenPage().registerNewARTRegimen(treatmentAdvice)
                .goToViewARTRegimenPage().goToChangeARTRegimenPage().reCreateARTRegimen(treatmentAdvice).logout();
    }

    public TestTreatmentAdvice getTreatmentAdvice(TestPatient patient, TestClinician clinician) {
        ShowARTRegimenPage showARTRegimenPage = viewPatient(patient, clinician).goToViewARTRegimenPage();
        TestTreatmentAdvice treatmentAdvice = new TestTreatmentAdvice().regimenName(showARTRegimenPage.getRegimenName()).drugCompositionName(showARTRegimenPage.getDrugCompositionGroupName());
        showARTRegimenPage.logout();
        return treatmentAdvice;
    }

    public void setupARTRegimenWithDependents(TestTreatmentAdvice treatmentAdvice, TestPatient patient, TestClinician clinician) {
        new ClinicianDataService(webDriver).createWithClinic(clinician);
        registerAndActivate(patient, clinician);
        createARTRegimen(treatmentAdvice, patient, clinician);
    }

    public void setInitialVitalStatistics(TestVitalStatistics testVitalStatistics, TestPatient patient, TestClinician clinician) {
        viewPatient(patient, clinician).clickVitalStatisticsLink_WhenPatientHasNone().enterVitalStatistics(testVitalStatistics).logout();
    }

    public void updateVitalStatistics(TestVitalStatistics testVitalStatistics, TestPatient patient, TestClinician clinician) {
        viewPatient(patient, clinician).clickVitalStatisticsLink_WhenPatientHasOne().goToEditVitalStatisticsPage().enterVitalStatistics(testVitalStatistics).logout();
    }

    public void changePatientToWeeklyPillReminderWithBestCallDay_AndBestCallTime(TestPatient patient, TestClinician clinician, String bestCallDay, String bestCallTime){
        viewPatient(patient, clinician).clickOnEditTAMAPreferences().changePatientToWeeklyCallPlanWithBestCallDayAndTime(bestCallDay, bestCallTime).logout();
    }

    public void changePatientToDailyPillReminder(TestPatient patient, TestClinician clinician){
        viewPatient(patient, clinician).clickOnEditTAMAPreferences().changePatientToDailyCallPlan().logout();
    }

    public TestVitalStatistics getInitialVitalStatistics(TestPatient patient, TestClinician clinician) {
        ShowVitalStatisticsPage showVitalStatisticsPage = viewPatient(patient, clinician).goToShowVitalStatisticsPage();
        TestVitalStatistics testVitalStatistics = new TestVitalStatistics()
                .weightInKg(showVitalStatisticsPage.getWeight())
                .heightInCm(showVitalStatisticsPage.getHeight())
                .systolicBp(showVitalStatisticsPage.getSystolicBp())
                .diastolicBp(showVitalStatisticsPage.getDiastolicBp())
                .temperatureInFahrenheit(showVitalStatisticsPage.getTemperature())
                .pulse(showVitalStatisticsPage.getPulse());
        showVitalStatisticsPage.logout();
        return testVitalStatistics;
    }

    public void setupLabResult(TestPatient patient, TestClinician clinician, TestLabResult labResult) {
        CreateLabResultsPage createLabResultsPage = viewPatient(patient, clinician).goToLabResultsPage();
        createLabResultsPage.registerNewLabResult(labResult);
        createLabResultsPage.logout();
    }

    public void createTestPatientForSymptomReporting(TestPatient patient, TestClinician clinician) {
        patient.patientPreferences().passcode("5678");

        registerAndActivate(patient, clinician);

        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));
        setupLabResult(patient, clinician, labResult);

        setInitialVitalStatistics(TestVitalStatistics.withMandatory(), patient, clinician);

        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        createARTRegimen(treatmentAdvice, patient, clinician);
    }
}
