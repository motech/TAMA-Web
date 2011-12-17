package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FilterCallLogsPage extends Page {
    @FindBy(how = How.ID, using = "_callLogStartDate_id")
    private WebElement startDate;

    @FindBy(how = How.ID, using = "_callLogEndDate_id")
    private WebElement endDate;

    @FindBy(how = How.ID, using = "nextToShowLogs")
    private WebElement getLogsButton;

    public FilterCallLogsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        startDate = WebDriverFactory.createWebElement(startDate);
        endDate = WebDriverFactory.createWebElement(endDate);
        getLogsButton = WebDriverFactory.createWebElement(getLogsButton);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("nextToShowLogs");
    }

    public ShowCallLogsPage filterCallLogs(Date startDate, Date endDate) {
        this.startDate.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(startDate));
        this.endDate.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(endDate));
        this.startDate.submit();
        return MyPageFactory.initElements(webDriver, ShowCallLogsPage.class);
    }

}
