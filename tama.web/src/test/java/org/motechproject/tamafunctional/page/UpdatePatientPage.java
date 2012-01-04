package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class UpdatePatientPage extends Page{

    @FindBy(how = How.ID, using = "weeklyReminderCall")
    private WebElement weeklyReminderCallRadioButton;

    @FindBy(how = How.ID, using = "_patientPreferences.dayOfWeeklyCall_id")
    private WebElement dayOfWeeklyCallBox;

    @FindBy(how = How.ID, using = "_patientPreferences.bestCallTime.timeOfDayAsString_id")
    private WebElement bestCallTimeBox;

    @FindBy(how = How.ID, using = "dailyReminderCall")
    private WebElement dailyReminderCallRadioButton;

    public UpdatePatientPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        weeklyReminderCallRadioButton = WebDriverFactory.createWebElement(weeklyReminderCallRadioButton);
        dailyReminderCallRadioButton = WebDriverFactory.createWebElement(dailyReminderCallRadioButton);
        dayOfWeeklyCallBox = WebDriverFactory.createWebElement(dayOfWeeklyCallBox);
        bestCallTimeBox = WebDriverFactory.createWebElement(bestCallTimeBox);
    }

    public ShowPatientPage changePatientToWeeklyCallPlanWithBestCallDayAndTime(String bestCallDay, String bestCallTime) {
        weeklyReminderCallRadioButton.click();
        dayOfWeeklyCallBox.sendKeys(bestCallDay);
        bestCallTimeBox.sendKeys(bestCallTime);
        bestCallTimeBox.submit();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public ShowPatientPage changePatientToDailyCallPlan() {
        dailyReminderCallRadioButton.click();
        bestCallTimeBox.submit();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}