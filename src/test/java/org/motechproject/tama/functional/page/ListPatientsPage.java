package org.motechproject.tama.functional.page;

import org.motechproject.tama.functional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;


public class ListPatientsPage extends Page {
    public final static String WELCOME_MESSAGE = "List all Patients";
    public static final String PATIENT_REGISTRATION_LINK_XPATH = "//li[@id='i_patient_new']/a";
    public static final String LIST_PATIENT_PANE_ID = "_title_pl_org_motechproject_tama_domain_patient_id";

    @FindBy(how = How.XPATH, using = PATIENT_REGISTRATION_LINK_XPATH)
    private WebElement patientRegistrationLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinic_new']/a")
    private WebElement clinicRegistrationLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinician_new']/a")
    private WebElement clinicianRegistrationLink;

    @FindBy(how = How.XPATH, using = "//h3")
    private WebElement welcomeMessage;

    @FindBy(how = How.ID, using = "_title_pl_org_motechproject_tama_domain_patient_id")
    private WebElement listPatientsPane;


    public ListPatientsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithXPATHToLoad(PATIENT_REGISTRATION_LINK_XPATH);
    }

    public PatientRegistrationPage goToPatientRegistrationPage() {
        patientRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, PatientRegistrationPage.class);
    }

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class);
    }

    public ClinicRegistrationPage goToClinicRegistrationPage() {
        clinicRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, ClinicRegistrationPage.class);
    }

    public ClinicianRegistrationPage goToClinicianRegistrationPage() {
        clinicianRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, ClinicianRegistrationPage.class);
    }

    public String getListPatientsPane() {
        return listPatientsPane.getText();
    }

    public ChangePasswordPage goToChangePasswordPage() {
        WebElement changePasswordLink = getNavigationLinks().findElement(By.id("changePasswordLink"));
        changePasswordLink.click();
        return MyPageFactory.initElements(webDriver, ChangePasswordPage.class);
    }
}
