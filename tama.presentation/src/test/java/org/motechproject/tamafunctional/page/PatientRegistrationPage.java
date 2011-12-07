package org.motechproject.tamafunctional.page;


import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class PatientRegistrationPage extends Page {

    private CreateBasicPatientInformationSection createBasicPatientInformationSection;
    private CreatePatientMedicalHistorySection createPatientMedicalHistorySection;
    private CreatePatientPreferencesSection createPatientPreferencesSection;

    public PatientRegistrationPage(WebDriver webDriver) {
        super(webDriver);
        createBasicPatientInformationSection = PageFactory.initElements(webDriver, CreateBasicPatientInformationSection.class);
        createPatientMedicalHistorySection = PageFactory.initElements(webDriver, CreatePatientMedicalHistorySection.class);
        createPatientPreferencesSection = PageFactory.initElements(webDriver, CreatePatientPreferencesSection.class);
    }

    @Override
    public void postInitialize() {
        createBasicPatientInformationSection.postInitialize();
        createPatientMedicalHistorySection.postInitialize();
        createPatientPreferencesSection.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad("_patientId_id", "dijitInputInner");
    }

    public ShowPatientPage registerNewPatient(TestPatient patient) {
        createBasicPatientInformationSection.enterDetails(patient);
        createPatientMedicalHistorySection.enterDetails(patient);
        createPatientPreferencesSection.enterDetails(patient);
        createPatientPreferencesSection.getPasscode().submit();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}
