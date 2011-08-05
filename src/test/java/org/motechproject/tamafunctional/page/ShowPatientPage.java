package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ShowPatientPage extends Page {
    public static final String PATIENT_ID_ID = "_s_org_motechproject_tama_domain_patient_patientId_patientId_id";

    @FindBy(how = How.ID, using = PATIENT_ID_ID)
    private WebElement patientId;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_mobilePhoneNumber_mobilePhoneNumber_id")
    private WebElement mobileNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_dateOfBirth_dateOfBirth_id")
    private WebElement dateOfBirth;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_test_reason_name_id")
    private WebElement hivTestReason;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_mode_of_transmission_type_id")
    private WebElement modeOfTransmission;

    @FindBy(how = How.CLASS_NAME, using = "drug_allergy_text")
    private WebElement allergy;

    @FindBy(how = How.CLASS_NAME, using = "drug_rash_text")
    private WebElement rash;

    @FindBy(how = How.ID, using = "activatePatient")
    private WebElement activationLink;

    @FindBy(how = How.ID, using = "clinic_visits")
    private WebElement clinicVisitsLink;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_Patient_status_status_id")
    private WebElement status;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;

    public ShowPatientPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PATIENT_ID_ID);
    }

    public String getPatientId() {
        return patientId.getText();
    }

    public String getMobileNumber() {
        return mobileNumber.getText();
    }

    public String getDateOfBirth() {
        return dateOfBirth.getText();
    }

    public String getHIVTestReason() {
        return hivTestReason.getText();
    }

    public String getModeOfTransmission() {
        return modeOfTransmission.getText();
    }

    public String getAllergyText() {
        return allergy.getText();
    }

    public String getRashText() {
        return rash.getText();
    }

    public String getStatus() {
        return status.getText();
    }

    public ShowPatientPage activatePatient() {
        this.activationLink.click();
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(PATIENT_ID_ID)) != null;
            }
        });
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public CreateARTRegimenPage goToCreateARTRegimenPage() {
        this.clinicVisitsLink.click();
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(CreateARTRegimenPage.REGIMEN_ID)) != null;
            }
        });
        return MyPageFactory.initElements(webDriver, CreateARTRegimenPage.class);
    }

    public ViewARTRegimenPage goToViewARTRegimenPage() {
        this.clinicVisitsLink.click();
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(ViewARTRegimenPage.REGIMEN_TEXT_ID)) != null;
            }
        });
        return MyPageFactory.initElements(webDriver, ViewARTRegimenPage.class);
    }

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class);
    }
}
