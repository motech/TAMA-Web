package org.motechproject.tamafunctionalframework.testdataservice;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.*;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;

public class PatientDataService extends EntityDataService {
    public PatientDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void register(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = registerWithoutLogout(patient, clinician);
        showPatientPage.logout();
    }

    public void registerAndActivate(TestTreatmentAdvice treatmentAdvice, TestPatient patient, TestClinician clinician) {
        activatePatient(patient, clinician).createNewRegimen(treatmentAdvice).logout();
    }

    public void registerAndActivate(TestTreatmentAdvice treatmentAdvice, TestLabResult labResult, TestPatient patient, TestClinician clinician) {
        activatePatient(patient, clinician).createNewRegimen(treatmentAdvice, labResult).logout();
    }

    public void registerAndActivate(TestTreatmentAdvice treatmentAdvice, TestVitalStatistics vitalStatistics, TestPatient patient, TestClinician clinician) {
        activatePatient(patient, clinician).createNewRegimen(treatmentAdvice, vitalStatistics).logout();
    }

    public void registerAndActivate(TestTreatmentAdvice treatmentAdvice, TestLabResult labResult, TestVitalStatistics vitalStatistics, TestPatient patient, TestClinician clinician) {
        activatePatient(patient, clinician).createNewRegimen(treatmentAdvice, labResult, vitalStatistics).logout();
    }

    private CreateClinicVisitPage activatePatient(TestPatient patient, TestClinician clinician) {
        return registerWithoutLogout(patient, clinician).activatePatient();
    }

    private ShowPatientPage registerWithoutLogout(TestPatient patient, TestClinician clinician) {
        PatientRegistrationPage patientRegistrationPage = login(clinician).goToPatientRegistrationPage();
        ShowPatientPage showPatientPage;
        if (patient.patientPreferences().isOnDailyCall())
            showPatientPage = patientRegistrationPage.registerNewPatientOnDailyPillReminder(patient);
        else
            showPatientPage = patientRegistrationPage.registerNewPatientOnWeekly(patient);
        patient.id(showPatientPage.patientDocId());
        logInfo("{Created}{Patient}{DocId=%s}", patient.id());
        return showPatientPage;
    }

    private ListPatientsPage login(TestClinician clinician) {
        return MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
    }

    private ShowPatientPage viewPatient(TestPatient patient, TestClinician clinician) {
        return login(clinician).gotoShowPatientPage(patient);
    }

    public void setupRegimenWithDependents(TestTreatmentAdvice treatmentAdvice, TestPatient patient, TestClinician clinician) {
        new ClinicianDataService(webDriver).createWithClinic(clinician);
        registerAndActivate(treatmentAdvice, patient, clinician);
    }

    public TestTreatmentAdvice getSavedTreatmentAdvice(TestPatient patient, TestClinician clinician) {
        ShowClinicVisitPage clinicVisitPage = viewPatient(patient, clinician).goToShowFirstClinicVisitPage();
        TestTreatmentAdvice treatmentAdvice = clinicVisitPage.getTreatmentAdvice();
        clinicVisitPage.logout();
        return treatmentAdvice;
    }

    public TestVitalStatistics getSavedVitalStatistics(TestPatient patient, TestClinician clinician) {
        ShowClinicVisitPage clinicVisitPage = viewPatient(patient, clinician).goToShowFirstClinicVisitPage();
        TestVitalStatistics vitalStatistics = clinicVisitPage.getVitalStatistics();
        clinicVisitPage.logout();
        return vitalStatistics;
    }

    public TestLabResult getSavedLabResult(TestPatient patient, TestClinician clinician) {
        ShowClinicVisitPage clinicVisitPage = viewPatient(patient, clinician).goToShowFirstClinicVisitPage();
        TestLabResult labResult = clinicVisitPage.getLabResult();
        clinicVisitPage.logout();
        return labResult;
    }

    public void changeRegimen(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice) {
        activatePatient(patient, clinician).createNewRegimen(treatmentAdvice).clickChangeRegimenLink().changeRegimen(treatmentAdvice).logout();
    }

    public void updateLabResults(TestPatient patient, TestClinician clinician, TestLabResult labResults) {
        viewPatient(patient, clinician).goToShowFirstClinicVisitPage().clickEditLabResultLink().update(labResults).gotoShowPatientPage().logout();
    }

    public void updateVitalStatistics(TestPatient patient, TestClinician clinician, TestVitalStatistics vitalStatistics) {
        viewPatient(patient, clinician).goToShowFirstClinicVisitPage().clickEditVitalStatisticsLink().enterVitalStatistics(vitalStatistics).logout();
    }
}
