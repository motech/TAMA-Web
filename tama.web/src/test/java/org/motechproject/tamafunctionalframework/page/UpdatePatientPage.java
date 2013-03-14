package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class UpdatePatientPage extends Page {

    @FindBy(how = How.ID, using = "weeklyReminderCall")
    private WebElement weeklyReminderCallRadioButton;

    @FindBy(how = How.ID, using = "_patientPreferences.dayOfWeeklyCall_id")
    private WebElement dayOfWeeklyCallBox;

    @FindBy(how = How.ID, using = "_patientPreferences.bestCallTime.timeOfDayAsString_id")
    private WebElement bestCallTimeBox;

    @FindBy(how = How.ID, using = "dailyReminderCall")
    private WebElement dailyReminderCallRadioButton;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement submit;

    private ConfirmCreationDialog confirmCreationDialog;

    private CreatePatientPreferencesSection createPatientPreferencesSection;

    public UpdatePatientPage(WebDriver webDriver) {
        super(webDriver);
        confirmCreationDialog = PageFactory.initElements(webDriver, ConfirmCreationDialog.class);
        createPatientPreferencesSection = PageFactory.initElements(webDriver, CreatePatientPreferencesSection.class);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("four_week_warning_confirm");
    }

    @Override
    public void postInitialize() {
        weeklyReminderCallRadioButton = WebDriverFactory.createWebElement(weeklyReminderCallRadioButton);
        dailyReminderCallRadioButton = WebDriverFactory.createWebElement(dailyReminderCallRadioButton);
        dayOfWeeklyCallBox = WebDriverFactory.createWebElement(dayOfWeeklyCallBox);
        bestCallTimeBox = WebDriverFactory.createWebElement(bestCallTimeBox);
        submit = WebDriverFactory.createWebElement(submit);

        createPatientPreferencesSection.postInitialize();
        confirmCreationDialog.postInitialize();
    }

    public ShowPatientPage changePatientToWeeklyCallPlanWithBestCallDayAndTime(String bestCallDay, String bestCallTime, boolean expectWarningDialog) {
        weeklyReminderCallRadioButton.click();
        dayOfWeeklyCallBox.sendKeys(bestCallDay);
        bestCallTimeBox.sendKeys(bestCallTime);
        createPatientPreferencesSection.getPasscode().submit();
        if (expectWarningDialog) {
            confirmCreationDialog.confirm();
        }
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public ShowPatientPage changePatientToDailyCallPlan() {
        dailyReminderCallRadioButton.click();
        submit.click();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}