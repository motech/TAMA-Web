package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ShowPatientPage {
    private WebDriver webDriver;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_patientId_patientId_id")
    private WebElement patientId;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_mobilePhoneNumber_mobilePhoneNumber_id")
    private WebElement mobileNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_dateOfBirth_dateOfBirth_id")
    private WebElement dateOfBirth;

    @FindBy(how = How.ID, using = "activatePatient")
    private WebElement activationLink;

   @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_Patient_status_status_id")
    private WebElement status;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;

    public ShowPatientPage(WebDriver webDriver) {
        this.webDriver = webDriver;
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

    public String getStatus() {
        return status.getText();
    }

    public ShowPatientPage activatePatient() {
        this.activationLink.click();
        return PageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return PageFactory.initElements(webDriver, ListPatientsPage.class);
    }
}
