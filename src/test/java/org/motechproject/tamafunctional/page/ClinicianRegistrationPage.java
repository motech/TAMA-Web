package org.motechproject.tamafunctional.page;

import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.MyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ClinicianRegistrationPage extends Page {
    @FindBy(how = How.ID, using = "_name_id")
    private WebElement name;

    @FindBy(how = How.ID, using = "_username_id")
    private WebElement username;

    @FindBy(how = How.ID, using = "_contactNumber_id")
    private WebElement contactNumber;

    @FindBy(how = How.ID, using = "_alternateContactNumber_id")
    private WebElement alternateContactNumber;

    @FindBy(how = How.ID, using = "_password_id")
    private WebElement password;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement registerClinicianLink;

    @FindBy(how = How.ID, using = "_clinic_id")
    private WebElement clinicElement;

    public ClinicianRegistrationPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_name_id");
    }

    @Override
    public void postInitialize() {
        name = new MyWebElement(name);
        clinicElement = new MyWebElement(clinicElement);
    }

    public ShowClinicianPage registerClinician(Clinician clinician) {
        name.sendKeys(clinician.getName());
        username.sendKeys(clinician.getUsername());
        contactNumber.sendKeys(clinician.getContactNumber());
        alternateContactNumber.sendKeys(clinician.getAlternateContactNumber());
        password.sendKeys(clinician.getPassword());
        Clinic clinic = clinician.getClinic();
        clinicElement.sendKeys(new StringBuilder().append(clinic.getName()).append(", ").append(clinic.getCity()).toString());
        registerClinicianLink.click();
        return MyPageFactory.initElements(webDriver, ShowClinicianPage.class);

    }
}
