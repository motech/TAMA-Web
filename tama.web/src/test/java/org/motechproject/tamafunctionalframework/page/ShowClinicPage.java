package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowClinicPage extends Page {
    public static final String CLINIC_NAME_NAME_ID = "_s_org_motechproject_tama_domain_Clinic_name_name_id";

    @FindBy(how = How.ID, id = CLINIC_NAME_NAME_ID)
    private WebElement name;

    @FindBy(how = How.ID, id = "_s_org_motechproject_tama_domain_Clinic_address_address_id")
    private WebElement address;

    @FindBy(how = How.ID, id = "_s_org_motechproject_tama_domain_Clinic_phone_phone_id")
    private WebElement phone;

    @FindBy(how = How.ID, id = "_s_org_motechproject_tama_domain_Clinic_city_city_id")
    private WebElement city;

    @FindBy(how = How.XPATH, xpath = "//a[@title='Home']")
    private WebElement homePageLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinician_new']/a")
    private WebElement clinicianRegistrationLink;

    public ShowClinicPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(CLINIC_NAME_NAME_ID);
    }

    public String getName() {
        return this.name.getText();

    }

    public String getAddress() {
        return address.getText();
    }

    public String getPhone() {
        return phone.getText();
    }

    public ClinicianRegistrationPage goToClinicianRegistrationPage() {
        clinicianRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, ClinicianRegistrationPage.class);
    }
}
