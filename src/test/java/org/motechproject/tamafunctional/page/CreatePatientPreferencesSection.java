package org.motechproject.tamafunctional.page;


import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreatePatientPreferencesSection {

    @FindBy(how = How.ID, using = "_patientPreferences.ivrLanguage_id")
    private WebElement ivrLanguage;
    @FindBy(how = How.ID, using = "_patientPreferences.passcode_id")
    private WebElement passcode;
    @FindBy(how = How.ID, using = "weeklyReminderCall")
    private WebElement weeklyReminderCall;
    @FindBy(how = How.ID, using = "_patientPreferences.dayOfWeeklyCall_id")
    private WebElement dayOfWeeklyCall;
    @FindBy(how = How.ID, using = "_patientPreferences.bestCallTime.timeOfDayAsString_id")
    private WebElement bestCallTime;

    public void postInitialize() {
        ivrLanguage = WebDriverFactory.createWebElement(ivrLanguage);
        passcode = WebDriverFactory.createWebElement(passcode);
        weeklyReminderCall = WebDriverFactory.createWebElement(weeklyReminderCall);
        dayOfWeeklyCall = WebDriverFactory.createWebElement(dayOfWeeklyCall);
        bestCallTime = WebDriverFactory.createWebElement(bestCallTime);
    }

    public void enterDetails(TestPatient patient) {
        passcode.clear();
        passcode.sendKeys(String.valueOf(patient.patientPreferences().passcode()));
        weeklyReminderCall.click();
        dayOfWeeklyCall.sendKeys(patient.patientPreferences().dayOfWeeklyCall());
        bestCallTime.sendKeys(patient.patientPreferences().bestCallTime());
    }
}
