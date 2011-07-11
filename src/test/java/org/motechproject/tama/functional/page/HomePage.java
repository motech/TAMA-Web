package org.motechproject.tama.functional.page;

import org.motechproject.tama.functional.framework.MyPageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;


public class HomePage extends Page {

    public final static String WELCOME_MESSAGE = "Welcome to Tama";
    public static final String PATIENT_REGISTRATION_LINK_XPATH = "//li[@id='i_patient_new']/a";

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

    public HomePage(WebDriver webDriver) {
        super(webDriver);
    }

    public PatientRegistrationPage goToPatientRegistrationPage() {
        patientRegistrationLink.click();
        return PageFactory.initElements(webDriver, PatientRegistrationPage.class);
    }

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return PageFactory.initElements(webDriver, ListPatientsPage.class);
    }

    public ClinicRegistrationPage goToClinicRegistrationPage() {
        clinicRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, ClinicRegistrationPage.class);
    }

    public ClinicianRegistrationPage goToClinicianRegistrationPage() {
        clinicianRegistrationLink.click();
        return PageFactory.initElements(webDriver, ClinicianRegistrationPage.class);
    }

    public String getWelcomeMessage() {
        return welcomeMessage.getText();
    }
}
