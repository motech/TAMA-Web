package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ClinicRegistrationPage extends Page {

    @FindBy(how = How.ID, using = "_name_id")
    private WebElement name;

    @FindBy(how = How.ID, using = "_address_id")
    private WebElement address;

    @FindBy(how = How.ID, using = "_phone_id")
    private WebElement phoneNumber;

    @FindBy(how = How.ID, using = "_city_id")
    private WebElement city;

    @FindBy(how = How.ID, using = "_clinicianContacts[0].name_id")
    private WebElement clinicianContact0Name;

    @FindBy(how = How.ID, using = "_clinicianContacts[0].phoneNumber_id")
    private WebElement clinicianContact0Number;

    @FindBy(how = How.ID, using = "_clinicianContacts[1].name_id")
    private WebElement clinicianContact1Name;

    @FindBy(how = How.ID, using = "_clinicianContacts[1].phoneNumber_id")
    private WebElement clinicianContact1Number;

    @FindBy(how = How.ID, using = "_clinicianContacts[2].name_id")
    private WebElement clinicianContact2Name;

    @FindBy(how = How.ID, using = "_clinicianContacts[2].phoneNumber_id")
    private WebElement clinicianContact2Number;


    @FindBy(how = How.ID, using = "proceed")
    private WebElement registerClinicLink;

    public ClinicRegistrationPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad("_name_id","dijitInputInner");
    }

    @Override
    public void postInitialize() {
        name = WebDriverFactory.createWebElement(name);
        address = WebDriverFactory.createWebElement(address);
        phoneNumber = WebDriverFactory.createWebElement(phoneNumber);
        city = WebDriverFactory.createWebElement(city);
        clinicianContact0Name = WebDriverFactory.createWebElement(clinicianContact0Name);
        clinicianContact0Number = WebDriverFactory.createWebElement(clinicianContact0Number);
        clinicianContact1Name = WebDriverFactory.createWebElement(clinicianContact1Name);
        clinicianContact1Number = WebDriverFactory.createWebElement(clinicianContact1Number);
        clinicianContact2Name = WebDriverFactory.createWebElement(clinicianContact2Name);
        clinicianContact2Number = WebDriverFactory.createWebElement(clinicianContact2Number);
    }

    public ShowClinicPage registerClinic(TestClinic clinic) {
        name.sendKeys(clinic.name());
        address.sendKeys(clinic.address());
        phoneNumber.sendKeys(clinic.phoneNumber());
        ((ExtendedWebElement)city).select(clinic.city());
        clinicianContact0Name.sendKeys(clinic.clinicianContact0Name());
        clinicianContact0Number.sendKeys(clinic.clinicianContact0Number());
        clinicianContact1Name.sendKeys(clinic.clinicianContact1Name());
        clinicianContact1Number.sendKeys(clinic.clinicianContact1Number());
        clinicianContact2Name.sendKeys(clinic.clinicianContact2Name());
        clinicianContact2Number.sendKeys(clinic.clinicianContact2Number());
        name.submit();
        return MyPageFactory.initElements(webDriver, ShowClinicPage.class);
    }
}
