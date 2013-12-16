package org.motechproject.tamafunctionalframework.page;


import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class PatientRegistrationPage extends Page {

    private CreateBasicPatientInformationSection createBasicPatientInformationSection;
    private CreatePatientMedicalHistorySection createPatientMedicalHistorySection;
    private CreatePatientPreferencesSection createPatientPreferencesSection;
    private ConfirmCreationDialog confirmCreationDialog;
    private ConfirmWarning confirmWarning;

    public PatientRegistrationPage(WebDriver webDriver) {
        super(webDriver);
        createBasicPatientInformationSection = PageFactory.initElements(webDriver, CreateBasicPatientInformationSection.class);
        createPatientMedicalHistorySection = PageFactory.initElements(webDriver, CreatePatientMedicalHistorySection.class);
        createPatientPreferencesSection = PageFactory.initElements(webDriver, CreatePatientPreferencesSection.class);
        confirmCreationDialog = PageFactory.initElements(webDriver, ConfirmCreationDialog.class);
        confirmWarning = PageFactory.initElements(webDriver, ConfirmWarning.class);
    }

    @Override
    public void postInitialize() {
        createBasicPatientInformationSection.postInitialize();
        createPatientMedicalHistorySection.postInitialize();
        createPatientPreferencesSection.postInitialize();
        confirmCreationDialog.postInitialize();
        confirmWarning.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad("_patientId_id", "dijitInputInner");
    }

    public ShowPatientPage registerNewPatientOnDailyPillReminder(TestPatient patient) {
        createBasicPatientInformationSection.enterDetails(patient);
        createPatientMedicalHistorySection.enterDetails(patient);
        createPatientPreferencesSection.enterDetails(patient);
        createPatientPreferencesSection.getPasscode().submit();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public ShowPatientPage registerNewPatientOnWeekly(TestPatient patient) {
        createBasicPatientInformationSection.enterDetails(patient);
        createPatientMedicalHistorySection.enterDetails(patient);
        createPatientPreferencesSection.enterDetails(patient);
        createPatientPreferencesSection.getPasscode().submit();
        confirmWarning.confirm();
        confirmCreationDialog.confirm();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}
