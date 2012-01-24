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

/*
 *TODO : Update operations should be removed from this class.
 *        Patient Update flows are specific to data, so the client is in a better position to do them.
 */
public class PatientDataService extends EntityDataService {
    public PatientDataService(WebDriver webDriver) {
        super(webDriver);
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
        createRegimen(treatmentAdvice, patient, clinician);
    }

    public void createRegimen(TestTreatmentAdvice treatmentAdvice, TestPatient patient, TestClinician clinician) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice).logout();
    }

    public void createRegimenWithVitalStatistics(TestTreatmentAdvice treatmentAdvice, TestVitalStatistics vitalStatistics, TestPatient patient, TestClinician clinician) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice, vitalStatistics).logout();
    }

    public void createRegimenWithLabResults(TestPatient patient, TestClinician clinician, TestTreatmentAdvice treatmentAdvice, TestLabResult labResult) {
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice, labResult).logout();
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

    public void updateVitalStatistics(TestPatient patient, TestClinician clinician, TestVitalStatistics vitalStatistics) {
        viewPatient(patient, clinician).goToShowClinicVisitPage().clickEditVitalStatisticsLink().enterVitalStatistics(vitalStatistics, webDriver).logout();
    }

    public void updateLabResults(TestPatient patient, TestClinician clinician, TestLabResult labResults) {
        viewPatient(patient, clinician).goToShowClinicVisitPage().clickEditLabResultLink().registerNewLabResult(labResults).logout();
    }

    public void createTestPatientForSymptomReporting(TestPatient patient, TestClinician clinician) {
        patient.patientPreferences().passcode("5678");

        registerAndActivate(patient, clinician);

        TestLabResult labResult = TestLabResult.withMandatory().results(Arrays.asList("60", "10"));
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));
        viewPatient(patient, clinician).goToCreateClinicVisitPage().createNewRegimen(treatmentAdvice, labResult, TestVitalStatistics.withMandatory()).logout();
    }
}
