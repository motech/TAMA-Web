package org.motechproject.tamafunctional.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ShowAlertPage extends Page{


    private static final String ALERT_DIV_ID = "_s_org_motechproject_tama_alert_id_id";

    public ShowAlertPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {

    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(ALERT_DIV_ID);
    }

    public String getAlertStatus() {
        return webDriver.findElement(By.id("_s_org_motechproject_tama_alert_symptomsStatus_symptomsAlertStatus_id")).getText();
    }

    public String getConnectedToDoctor() {
        return webDriver.findElement(By.id("_s_org_motechproject_tama_alert_connectedToDoctor_connectedToDoctor_id")).getText();
    }

    public String getNotes() {
        return webDriver.findElement(By.id("_s_org_motechproject_tama_alert_notes_notes_id")).getText();
    }
}
