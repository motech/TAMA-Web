package org.motechproject.tamafunctional.page;

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
    }

    public ShowClinicPage registerClinic(TestClinic clinic) {
        name.sendKeys(clinic.name());
        address.sendKeys(clinic.address());
        phoneNumber.sendKeys(clinic.phoneNumber());
        city.clear();
        city.sendKeys(clinic.city());
        registerClinicLink.click();
        return MyPageFactory.initElements(webDriver, ShowClinicPage.class);
    }
}
