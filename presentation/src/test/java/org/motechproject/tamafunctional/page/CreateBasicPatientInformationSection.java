package org.motechproject.tamafunctional.page;


import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
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
    @FindBy(how = How.ID, using = "nextToMedicalHistory")
    private WebElement nextToMedicalHistory;

    public void postInitialize() {
        patientId = WebDriverFactory.createWebElement(patientId);
        mobileNumber = WebDriverFactory.createWebElement(mobileNumber);
        dateOfBirth = WebDriverFactory.createWebElement(dateOfBirth);
        travelTimeInDays = WebDriverFactory.createWebElement(travelTimeInDays);
        travelTimeInHrs = WebDriverFactory.createWebElement(travelTimeInHrs);
        travelTimeInMins = WebDriverFactory.createWebElement(travelTimeInMins);
        gender = WebDriverFactory.createWebElement(gender);
    }

    public void enterDetails(TestPatient patient) {
        patientId.sendKeys(patient.patientId());
        mobileNumber.sendKeys(patient.mobileNumber());
        dateOfBirth.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()));
        ((ExtendedWebElement)travelTimeInDays).select(patient.travelTimeToClinicInDays());
        ((ExtendedWebElement)travelTimeInHrs).select(patient.travelTimeToClinicInHours());
        ((ExtendedWebElement)travelTimeInMins).select(String.valueOf(patient.travelTimeToClinicInMinutes()));

        nextToMedicalHistory.click();
    }
}
