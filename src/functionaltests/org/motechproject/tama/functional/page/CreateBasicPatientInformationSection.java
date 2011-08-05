package org.motechproject.tama.functional.page;


import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.MyWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.text.SimpleDateFormat;

public class CreateBasicPatientInformationSection {

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
    @FindBy(how = How.ID, using = "nextToMedicalHistory")
    private WebElement nextToMedicalHistory;

    public void postInitialize() {
        patientId = new MyWebElement(patientId);
        mobileNumber = new MyWebElement(mobileNumber);
        dateOfBirth = new MyWebElement(dateOfBirth);
        travelTimeInDays = new MyWebElement(travelTimeInDays);
        travelTimeInHrs = new MyWebElement(travelTimeInHrs);
        travelTimeInMins = new MyWebElement(travelTimeInMins);
        gender = new MyWebElement(gender);
    }

    public void enterDetails(Patient patient) {
        patientId.sendKeys(patient.getPatientId());
        mobileNumber.sendKeys(patient.getMobilePhoneNumber());
        dateOfBirth.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(patient.getDateOfBirth()));
        travelTimeInDays.clear();
        travelTimeInDays.sendKeys(String.valueOf(patient.getTravelTimeToClinicInDays()));
        travelTimeInHrs.clear();
        travelTimeInHrs.sendKeys(String.valueOf(patient.getTravelTimeToClinicInHours()));
        travelTimeInMins.clear();
        travelTimeInMins.sendKeys(String.valueOf(patient.getTravelTimeToClinicInMinutes()));

        nextToMedicalHistory.click();
    }
}
