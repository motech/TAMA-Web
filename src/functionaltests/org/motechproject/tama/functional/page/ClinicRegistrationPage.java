package org.motechproject.tama.functional.page;

import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebElement;
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
        waitForElementWithIdToLoad("_name_id");
    }

    @Override
    public void postInitialize() {
        name = new MyWebElement(name);
        address = new MyWebElement(address);
        phoneNumber = new MyWebElement(phoneNumber);
        city = new MyWebElement(city);
    }

    public ShowClinicPage registerClinic(Clinic clinic) {
        name.sendKeys(clinic.getName());
        address.sendKeys(clinic.getAddress());
        phoneNumber.sendKeys(clinic.getPhone());
        city.clear();
        city.sendKeys(clinic.getCity().getName());
        registerClinicLink.click();
        return MyPageFactory.initElements(webDriver, ShowClinicPage.class);
    }
}
