package org.motechproject.tamafunctionalframework.page;


import org.motechproject.tamafunctionalframework.framework.ExtendedWebElement;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestPatientPreferences;
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
        if (patient.patientPreferences().callPreference().equals(TestPatientPreferences.CallPreference.WEEKLY_CALL)) {
            weeklyReminderCall.click();
            ((ExtendedWebElement) dayOfWeeklyCall).select(patient.patientPreferences().dayOfWeeklyCall());
            bestCallTime.sendKeys(patient.patientPreferences().bestCallTime());
        }
    }

    public WebElement getPasscode() {
        return passcode;
    }
}
