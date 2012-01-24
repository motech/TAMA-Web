package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.*;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;

public class PatientDataService extends EntityDataService {
    public PatientDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void register(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = registerWithoutLogout(patient, clinician);
        showPatientPage.logout();
    }

    public void registerAndActivate(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = registerWithoutLogout(patient, clinician);
        showPatientPage.activatePatient().logout();
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
        registerAndActivate(patient, clinician);
        createRegimen(patient, clinician, treatmentAdvice);
    }

    public void createRegimen(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice).logout();
    }

    public void createRegimen(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice, TestVitalStatistics vitalStatistics) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice, vitalStatistics).logout();
    }

    public void createRegimen(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice, TestLabResult labResult) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice, labResult).logout();
    }

    public void createRegimen(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice, TestLabResult labResult, TestVitalStatistics vitalStatistics) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice, labResult, vitalStatistics).logout();
    }

    public TestTreatmentAdvice getSavedTreatmentAdvice(TestPatient patient, TestClinician clinician) {
        ShowClinicVisitPage clinicVisitPage = viewPatient(patient, clinician).goToShowClinicVisitPage();
        TestTreatmentAdvice treatmentAdvice = clinicVisitPage.getTreatmentAdvice();
        clinicVisitPage.logout();
        return treatmentAdvice;
    }

    public TestVitalStatistics getSavedVitalStatistics(TestPatient patient, TestClinician clinician) {
        ShowClinicVisitPage clinicVisitPage = viewPatient(patient, clinician).goToShowClinicVisitPage();
        TestVitalStatistics vitalStatistics = clinicVisitPage.getVitalStatistics();
        clinicVisitPage.logout();
        return vitalStatistics;
    }

    public TestLabResult getSavedLabResult(TestPatient patient, TestClinician clinician) {
        ShowClinicVisitPage clinicVisitPage = viewPatient(patient, clinician).goToShowClinicVisitPage();
        TestLabResult labResult = clinicVisitPage.getLabResult();
        clinicVisitPage.logout();
        return labResult;
    }

    public void changeRegimen(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice)
                .goToShowClinicVisitPage().clickChangeRegimenLink().changeRegimen(treatmentAdvice).logout();
    }

    public void updateVitalStatistics(TestPatient patient, TestClinician clinician, TestVitalStatistics vitalStatistics) {
        viewPatient(patient, clinician).goToShowClinicVisitPage().clickEditVitalStatisticsLink().enterVitalStatistics(vitalStatistics, webDriver).logout();
    }

    public void updateLabResults(TestPatient patient, TestClinician clinician, TestLabResult labResults) {
        viewPatient(patient, clinician).goToShowClinicVisitPage().clickEditLabResultLink().update(labResults).logout();
    }
}
