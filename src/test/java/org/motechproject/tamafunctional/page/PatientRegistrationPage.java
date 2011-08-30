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

    @FindBy(how = How.ID, using = "proceed")
    private WebElement savePatient;
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
        savePatient = WebDriverFactory.createWebElement(savePatient);
        createBasicPatientInformationSection.postInitialize();
        createPatientMedicalHistorySection.postInitialize();
        createPatientPreferencesSection.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_patientId_id");
    }

    public ShowPatientPage registerNewPatient(TestPatient patient) {
        createBasicPatientInformationSection.enterDetails(patient);
        createPatientMedicalHistorySection.enterDetails(patient);
        createPatientPreferencesSection.enterDetails(patient);
        savePatient.click();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}
