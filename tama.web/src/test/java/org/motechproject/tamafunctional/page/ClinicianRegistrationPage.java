package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
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
        waitForDojoElementToLoad("_name_id", "dijitInputInner");
    }

    @Override
    public void postInitialize() {
        name = WebDriverFactory.createWebElement(name);
        clinicElement = WebDriverFactory.createWebElement(clinicElement);
    }

    public ShowClinicianPage registerClinician(TestClinician clinician) {
        name.sendKeys(clinician.name());
        username.sendKeys(clinician.userName());
        contactNumber.sendKeys(clinician.contactNumber());
        alternateContactNumber.sendKeys(clinician.alternateContactNumber());
        password.sendKeys(clinician.password());
        TestClinic clinic = clinician.clinic();
        ((ExtendedWebElement) clinicElement).select(new StringBuilder().append(clinic.name()).append(", ").append(clinic.city()).toString());
        name.submit();
        return MyPageFactory.initElements(webDriver, ShowClinicianPage.class);
    }
}
