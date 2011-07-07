package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowClinicianPage {

    private WebDriver webDriver;

    public ShowClinicianPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_name_name_id")
    private WebElement name;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_contactnumber_contactNumber_id")
    private WebElement contactNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_alternateContactNumber_alternateContactNumber_id")
    private WebElement alternateContactNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_username_username_id")
    private WebElement username;


    public String getName() {
        return name.getText();
    }

    public String getContactNumber() {
        return contactNumber.getText();
    }

    public String getAlternateContactNumber() {
        return alternateContactNumber.getText();
    }

    public String getRole() {
        return "";
    }

    public String getUsername() {
        return username.getText();
    }
}
