package org.motechproject.tama.functional.page;

import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

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

    public ClinicianRegistrationPage(WebDriver webDriver) {
        super(webDriver);
    }

    public ShowClinicianPage registerClinician(Clinician clinician) {
        name.sendKeys(clinician.getName());
        username.sendKeys(clinician.getUsername());
        contactNumber.sendKeys(clinician.getContactNumber());
        alternateContactNumber.sendKeys(clinician.getAlternateContactNumber());
        password.sendKeys(clinician.getPassword());
        registerClinicianLink.click();
        return PageFactory.initElements(webDriver, ShowClinicianPage.class);

    }
}
