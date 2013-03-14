package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.ExtendedWebElement;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class UpdateAlertPage extends Page {
    public static final String SYMPTOMS_ALERT_STATUS_ID = "_alertStatus_id";
    public static final String NOTES_ID = "_notes_id";

    @FindBy(how = How.ID, using = SYMPTOMS_ALERT_STATUS_ID)
    private WebElement symptomReportingAlertStatus;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement proceed;

    @FindBy(how = How.ID, using = NOTES_ID)
    private WebElement notes;

    public UpdateAlertPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        this.symptomReportingAlertStatus = WebDriverFactory.createWebElement(this.symptomReportingAlertStatus);
        this.proceed = WebDriverFactory.createWebElement(this.proceed);
        this.notes = WebDriverFactory.createWebElement(this.notes);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(NOTES_ID);
    }

    public void changeSymptomReportingAlertStatus(String status) {
        ((ExtendedWebElement) symptomReportingAlertStatus).select(status);
    }

    public ShowAlertPage save() {
        proceed.click();
        return MyPageFactory.initElements(webDriver, ShowAlertPage.class);
    }

    public void changeNotes(String notes) {
        this.notes.clear();
        this.notes.sendKeys(notes);
    }
}
