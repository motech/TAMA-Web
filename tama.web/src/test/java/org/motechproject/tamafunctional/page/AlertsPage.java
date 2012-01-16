package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

import static junit.framework.Assert.assertTrue;

public class AlertsPage extends Page {

    public static final String LIST_ALERT_PANE_ID = "_title_pl_org_motechproject_tama_domain_patientalert_id_pane";

    @FindBy(how = How.ID, using = "_startDate_id")
    private WebElement startDate;

    @FindBy(how = How.ID, using = "searchByAlertStatus")
    private WebElement alertStatus;

    public AlertsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        startDate = WebDriverFactory.createWebElement(startDate);
        alertStatus = WebDriverFactory.createWebElement(alertStatus);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_startDate_id");
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

    public AlertsPage filterUnreadAlerts() {
        alertStatus.sendKeys("Unread");
        startDate.submit();
        waitForElementWithIdToLoad(LIST_ALERT_PANE_ID);
        return MyPageFactory.initElements(webDriver, AlertsPage.class);
    }

    public AlertsPage filterReadAlerts() {
        alertStatus.sendKeys("Read");
        startDate.submit();
        waitForElementWithIdToLoad(LIST_ALERT_PANE_ID);
        return MyPageFactory.initElements(webDriver, AlertsPage.class);
    }
}
