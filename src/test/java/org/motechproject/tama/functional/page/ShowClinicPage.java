package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowClinicPage {

    private WebDriver webDriver;

    @FindBy(how = How.ID,  id = "_s_org_motechproject_tama_domain_Clinic_name_name_id")
    private WebElement name;

    @FindBy(how = How.ID,  id = "_s_org_motechproject_tama_domain_Clinic_address_address_id")
    private WebElement address;

    @FindBy(how = How.ID,  id = "_s_org_motechproject_tama_domain_Clinic_phone_phone_id")
    private WebElement phone;

    @FindBy(how = How.ID,  id = "_s_org_motechproject_tama_domain_Clinic_city_city_id")
    private WebElement city;

    public ShowClinicPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getName() {
        return this.name.getText();

    }
    public String getAddress() {
        return address.getText();
    }

    public String getPhone(){
        return  phone.getText();
    }

    public String getCity() {
        return city.getText();
    }
}
