package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowClinicianPage extends Page {

    public static final String CLINICIAN_NAME_NAME_ID = "_s_org_motechproject_tama_domain_Clinician_name_name_id";

    @FindBy(how = How.ID, using = CLINICIAN_NAME_NAME_ID)
    private WebElement name;


    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_contactnumber_contactNumber_id")
    private WebElement contactNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_alternateContactNumber_alternateContactNumber_id")
    private WebElement alternateContactNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_Clinician_username_username_id")
    private WebElement username;

    @FindBy(how = How.ID, using = "setPasswordLink")
    private WebElement changePasswordLink;

    public ShowClinicianPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(CLINICIAN_NAME_NAME_ID);
    }

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

    public SetClinicianPasswordPage goToSetClinicianPasswordPage() {
        changePasswordLink.click();
        return MyPageFactory.initElements(webDriver, SetClinicianPasswordPage.class);
    }
}
