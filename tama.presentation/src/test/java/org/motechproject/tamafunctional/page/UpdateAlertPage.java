package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class UpdateAlertPage extends Page{
    public static final String SYMPTOMS_ALERT_STATUS_ID = "_symptomsAlertStatus_id";
    public static final String NOTES_ID = "_notes_id";

    @FindBy(how = How.ID, using = SYMPTOMS_ALERT_STATUS_ID)
    private WebElement alertStatus;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement proceed;

    @FindBy(how = How.ID, using = NOTES_ID)
    private WebElement notes;

    public UpdateAlertPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        this.alertStatus = WebDriverFactory.createWebElement(this.alertStatus);
        this.proceed = WebDriverFactory.createWebElement(this.proceed);
        this.notes = WebDriverFactory.createWebElement(this.notes);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(SYMPTOMS_ALERT_STATUS_ID);
        waitForElementWithIdToLoad(NOTES_ID);
    }


    public void changeAlertStatus(String status) {
        ((ExtendedWebElement)alertStatus).select(status);
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
