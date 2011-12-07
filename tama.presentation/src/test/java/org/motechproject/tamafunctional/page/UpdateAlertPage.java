package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UpdateAlertPage extends Page{
    public static final String SYMPTOMS_ALERT_STATUS_ID = "_symptomsAlertStatus_id";
    public static final String NOTES_ID = "_notes_id";

    public UpdateAlertPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {

    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(SYMPTOMS_ALERT_STATUS_ID);
        waitForElementWithIdToLoad(NOTES_ID);
    }


    public void changeAlertStatus(String status) {
        webDriver.findElement(By.id(SYMPTOMS_ALERT_STATUS_ID)).clear();
        webDriver.findElement(By.id(SYMPTOMS_ALERT_STATUS_ID)).sendKeys(status);
        webDriver.findElement(By.id(SYMPTOMS_ALERT_STATUS_ID)).click();   // required to simulate onKeyPress that sets the dojo backing element
    }

    public ShowAlertPage save() {
        webDriver.findElement(By.id("proceed")).click();
        return MyPageFactory.initElements(webDriver, ShowAlertPage.class);
    }

    public void changeNotes(String notes) {
        webDriver.findElement(By.id(NOTES_ID)).clear();
        webDriver.findElement(By.id(NOTES_ID)).sendKeys(notes);
    }
}
