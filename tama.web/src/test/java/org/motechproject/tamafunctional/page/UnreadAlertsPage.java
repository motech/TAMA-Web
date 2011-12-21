package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.Assert.assertTrue;

public class UnreadAlertsPage extends Page {

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

    public ShowAlertPage openShowAlertPage(String patientId) {
        int rowId = getRowId(alertsTable(), patientId);
        assertTrue(rowId >= 0);
        WebElement trElement = alertsTable().get(rowId);
        //open
        List<WebElement> elementsWithLinks = trElement.findElements(By.xpath("td/a"));
        elementsWithLinks.get(0).click();
        return MyPageFactory.initElements(webDriver, ShowAlertPage.class);
    }

    public UpdateAlertPage openUpdateAlertPage(String patientId) {
        int rowId = getRowId(alertsTable(), patientId);
        assertTrue(rowId >= 0);
        WebElement trElement = alertsTable().get(rowId);
        //open
        List<WebElement> elementsWithLinks = trElement.findElements(By.xpath("td/a"));
        elementsWithLinks.get(1).click();
        return MyPageFactory.initElements(webDriver, UpdateAlertPage.class);
    }

    private int getRowId(List<WebElement> webElements, String patientId) {
        int rowId = 0;
        for (WebElement trElement : webElements) {
            List<WebElement> td_collection = trElement.findElements(By.xpath("td"));
            String actualPatientId = td_collection.get(0).getText();
            if (patientId.equals(actualPatientId)) {
                return rowId;
            }
            rowId++;
        }
        return -1;
    }
}