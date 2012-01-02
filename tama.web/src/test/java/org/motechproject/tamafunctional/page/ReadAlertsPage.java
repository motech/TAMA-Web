package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

public class ReadAlertsPage extends Page {

    public static final String LIST_ALERT_PANE_ID = "_title_pl_org_motechproject_tama_domain_patientalert_id_pane";

    @FindBy(how = How.ID, using = "_startDate_id")
    private WebElement startDate;

    public ReadAlertsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        startDate = WebDriverFactory.createWebElement(startDate);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_startDate_id");
    }

    public List<WebElement> alertsTable() {
        return webDriver.findElement(By.id(LIST_ALERT_PANE_ID)).findElements(By.xpath("id('" + LIST_ALERT_PANE_ID + "')/table/tbody/tr"));
    }

    public ReadAlertsPage filter() {
        startDate.submit();
        waitForElementWithIdToLoad(LIST_ALERT_PANE_ID);
        return MyPageFactory.initElements(webDriver, ReadAlertsPage.class);
    }
}
