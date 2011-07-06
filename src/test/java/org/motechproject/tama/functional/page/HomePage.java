package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;


public class HomePage {

    private WebDriver webDriver;

    public final static String WELCOME_MESSAGE = "Welcome to Tama";

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_new']/a")
    private WebElement patientRegistrationLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_clinic_new']/a")
    private WebElement clinicRegistrationLink;

    @FindBy(how = How.XPATH, using = "//h3")
    private WebElement welcomeMessage;

    public HomePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public PatientRegistrationPage goToPatientRegistrationPage() {
        patientRegistrationLink.click();
        return PageFactory.initElements(webDriver, PatientRegistrationPage.class);
    }

    public ClinicRegistrationPage goToClinicRegistrationPage() {
        clinicRegistrationLink.click();
        return PageFactory.initElements(webDriver, ClinicRegistrationPage.class);
    }

    public String getWelcomeMessage() {
        return welcomeMessage.getText();
    }
}
