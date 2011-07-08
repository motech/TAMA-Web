package org.motechproject.tama.functional.page;


import java.text.SimpleDateFormat;

import org.motechproject.tama.domain.Patient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class PatientRegistrationPage extends Page {

    @FindBy(how = How.ID, using = "_patientId_id")
    private WebElement patientId;
    @FindBy(how = How.ID, using = "_mobilePhoneNumber_id")
    private WebElement mobileNumber;
    @FindBy(how = How.ID, using = "_dateOfBirth_id")
    private WebElement dateOfBirth;
    @FindBy(how = How.ID, using = "_travelTimeToClinicInDays_id")
    private WebElement travelTimeInDays;
    @FindBy(how = How.ID, using = "_travelTimeToClinicInHours_id")
    private WebElement travelTimeInHrs;
    @FindBy(how = How.ID, using = "_travelTimeToClinicInMinutes_id")
    private WebElement travelTimeInMins;
    @FindBy(how = How.ID, using = "_gender_id")
    private WebElement gender;
    @FindBy(how = How.ID, using = "_ivrLanguage_id")
    private WebElement ivrLanguage;
    @FindBy(how = How.ID, using = "_passcode_id")
    private WebElement passcode;

    public PatientRegistrationPage(WebDriver webDriver) {
        super(webDriver);
    }

    public ShowPatientPage registerNewPatient(Patient patient){
        patientId.sendKeys(patient.getPatientId());
        mobileNumber.sendKeys(patient.getMobilePhoneNumber());
        dateOfBirth.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(patient.getDateOfBirth()));
        travelTimeInDays.clear();
        travelTimeInDays.sendKeys(String.valueOf(patient.getTravelTimeToClinicInDays()));
        travelTimeInHrs.clear();
        travelTimeInHrs.sendKeys(String.valueOf(patient.getTravelTimeToClinicInHours()));
        travelTimeInMins.clear();
        travelTimeInMins.sendKeys(String.valueOf(patient.getTravelTimeToClinicInMinutes()));
        passcode.clear();
        passcode.sendKeys(String.valueOf(patient.getPasscode()));
        patientId.submit();
        return PageFactory.initElements(webDriver, ShowPatientPage.class);
    }

}
