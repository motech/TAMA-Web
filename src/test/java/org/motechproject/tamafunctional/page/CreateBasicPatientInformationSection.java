package org.motechproject.tamafunctional.page;


import org.motechproject.tama.domain.Patient;
import org.motechproject.tamafunctional.framework.MyWebElement;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.text.SimpleDateFormat;

public class CreateBasicPatientInformationSection {

    @FindBy(how = How.ID, using = "_patientId_id")
    private WebElement patientId;
    @FindBy(how = How.ID, using = "_mobilePhoneNumber_id")
    private WebElement mobileNumber;
    @FindBy(how = How.ID, using = "_dateOfBirthAsDate_id")
    private WebElement dateOfBirth;
    @FindBy(how = How.ID, using = "_travelTimeToClinicInDays_id")
    private WebElement travelTimeInDays;
    @FindBy(how = How.ID, using = "_travelTimeToClinicInHours_id")
    private WebElement travelTimeInHrs;
    @FindBy(how = How.ID, using = "_travelTimeToClinicInMinutes_id")
    private WebElement travelTimeInMins;
    @FindBy(how = How.ID, using = "_gender_id")
    private WebElement gender;
    @FindBy(how = How.ID, using = "nextToPatientPreferences")
    private WebElement nextToPatientPreferences;

//    #252
//    @FindBy(how = How.ID, using = "nextToMedicalHistory")
//    private WebElement nextToMedicalHistory;

    public void postInitialize() {
        patientId = new MyWebElement(patientId);
        mobileNumber = new MyWebElement(mobileNumber);
        dateOfBirth = new MyWebElement(dateOfBirth);
        travelTimeInDays = new MyWebElement(travelTimeInDays);
        travelTimeInHrs = new MyWebElement(travelTimeInHrs);
        travelTimeInMins = new MyWebElement(travelTimeInMins);
        gender = new MyWebElement(gender);
    }

    public void enterDetails(TestPatient patient) {
        patientId.sendKeys(patient.patientId());
        mobileNumber.sendKeys(patient.mobileNumber());
        dateOfBirth.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()));
        travelTimeInDays.clear();
        travelTimeInDays.sendKeys(patient.travelTimeToClinicInDays());
        travelTimeInHrs.clear();
        travelTimeInHrs.sendKeys(patient.travelTimeToClinicInHours());
        travelTimeInMins.clear();
        travelTimeInMins.sendKeys(String.valueOf(patient.travelTimeToClinicInMinutes()));

        nextToPatientPreferences.click();
    }
}
