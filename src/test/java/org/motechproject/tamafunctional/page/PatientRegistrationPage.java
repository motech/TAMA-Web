package org.motechproject.tamafunctional.page;


import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class PatientRegistrationPage extends Page {

    @FindBy(how = How.ID, using = "_patientId_id")
    private WebElement patientId;
    private CreateBasicPatientInformationSection createBasicPatientInformationSection;
//    #252
//    private CreatePatientMedicalHistorySection createPatientMedicalHistorySection;
    private CreatePatientPreferencesSection createPatientPreferencesSection;

    public PatientRegistrationPage(WebDriver webDriver) {
        super(webDriver);
        createBasicPatientInformationSection = PageFactory.initElements(webDriver, CreateBasicPatientInformationSection.class);
//    #252
//        createPatientMedicalHistorySection = PageFactory.initElements(webDriver, CreatePatientMedicalHistorySection.class);
        createPatientPreferencesSection = PageFactory.initElements(webDriver, CreatePatientPreferencesSection.class);
    }

    @Override
    public void postInitialize() {
        patientId = WebDriverFactory.createWebElement(patientId);
        createBasicPatientInformationSection.postInitialize();
//    #252
//        createPatientMedicalHistorySection.postInitialize();
        createPatientPreferencesSection.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_patientId_id");
    }

    public ShowPatientPage registerNewPatient(TestPatient patient) {
        createBasicPatientInformationSection.enterDetails(patient);
//    #252
//        createPatientMedicalHistorySection.enterDetails(patient);
        createPatientPreferencesSection.enterDetails(patient);
        patientId.submit();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}
