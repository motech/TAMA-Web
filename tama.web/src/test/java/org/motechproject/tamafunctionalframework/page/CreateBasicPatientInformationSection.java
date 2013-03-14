package org.motechproject.tamafunctionalframework.page;


import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
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
        gender = WebDriverFactory.createWebElement(gender);
    }

    public void enterDetails(TestPatient patient) {
        patientId.sendKeys(patient.patientId());
        mobileNumber.sendKeys(patient.mobileNumber());
        dateOfBirth.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()));
        travelTimeInDays.sendKeys(patient.travelTimeToClinicInDays());
        travelTimeInHrs.sendKeys((patient.travelTimeToClinicInHours()));
        travelTimeInMins.sendKeys((String.valueOf(patient.travelTimeToClinicInMinutes())));
        nextToMedicalHistory.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
