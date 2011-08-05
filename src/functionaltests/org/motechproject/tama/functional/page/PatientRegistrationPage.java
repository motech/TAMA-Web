package org.motechproject.tama.functional.page;


import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class PatientRegistrationPage extends Page {

    @FindBy(how = How.ID, using = "_patientId_id")
    private WebElement patientId;
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
        patientId = new MyWebElement(patientId);
        createBasicPatientInformationSection.postInitialize();
        createPatientMedicalHistorySection.postInitialize();
        createPatientPreferencesSection.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_patientId_id");
    }

    public ShowPatientPage registerNewPatient(Patient patient) {
        createBasicPatientInformationSection.enterDetails(patient);
        createPatientMedicalHistorySection.enterDetails(patient);
        createPatientPreferencesSection.enterDetails(patient);
        patientId.submit();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}
