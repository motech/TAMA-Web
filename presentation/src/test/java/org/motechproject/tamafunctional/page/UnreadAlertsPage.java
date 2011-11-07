package org.motechproject.tamafunctional.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class UnreadAlertsPage extends Page{

    public static final String LIST_ALERT_PANE_ID = "_title_pl_org_motechproject_tama_domain_patientalert_id_pane";

    public UnreadAlertsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {

    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(LIST_ALERT_PANE_ID);
    }

    public List<WebElement> alertsTable() {
        return webDriver.findElement(By.id(LIST_ALERT_PANE_ID)).findElements(By.xpath("id('" + LIST_ALERT_PANE_ID + "')/table/tbody/tr"));
    }
}
