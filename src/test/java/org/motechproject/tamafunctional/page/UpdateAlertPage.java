package org.motechproject.tamafunctional.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class UpdateAlertPage extends Page{


    public UpdateAlertPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {

    }

    @Override
    protected void waitForPageToLoad() {
        //waitForElementWithIdToLoad(LIST_ALERT_PANE_ID);
    }

    public List<WebElement> alertsTable() {
        return null;//webDriver.findElement(By.id(LIST_ALERT_PANE_ID)).findElements(By.xpath("id('" + LIST_ALERT_PANE_ID + "')/table/tbody/tr"));
    }
}
