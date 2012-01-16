package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;


public class ListClinicsPage extends Page {
    public final static String WELCOME_MESSAGE = "Clinics";
    public static final String CLINICS_LISTING_LINK_XPATH = "//li[@id='i_clinic_new']/a";

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinic_new']/a")
    private WebElement clinicRegistrationLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinician_new']/a")
    private WebElement clinicianRegistrationLink;

    @FindBy(how = How.ID, using = "_title_pl_org_motechproject_tama_domain_Clinic_id")
    private WebElement listClinicsPane;

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinician_list']/a")
    private WebElement listCliniciansLink;

    public ListClinicsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithXPATHToLoad(CLINICS_LISTING_LINK_XPATH);
    }

    public ClinicianRegistrationPage goToClinicianRegistrationPage() {
        clinicianRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, ClinicianRegistrationPage.class);
    }

    public ClinicRegistrationPage goToClinicRegistrationPage() {
        clinicRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, ClinicRegistrationPage.class);
    }

    public ChangePasswordPage goToChangePasswordPage() {
        WebElement changePasswordLink = getNavigationLinks().findElement(By.id("changePasswordLink"));
        changePasswordLink.click();
        return MyPageFactory.initElements(webDriver, ChangePasswordPage.class);
    }

    public String getListClinicsPane() {
        return listClinicsPane.getText();
    }

    public ListCliniciansPage goToListCliniciansPage() {
        listCliniciansLink.click();
        return MyPageFactory.initElements(webDriver, ListCliniciansPage.class);
    }
}
