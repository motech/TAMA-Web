package org.motechproject.tama.functional.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowPatientPage {
    private WebDriver webDriver;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Patient_patientId_patientId_id")
    private WebElement patientId;
    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Patient_mobilePhoneNumber_mobilePhoneNumber_id")
    private WebElement mobileNumber;
    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Patient_dateOfBirth_dateOfBirth_id")
    private WebElement dateOfBirth;

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
}
