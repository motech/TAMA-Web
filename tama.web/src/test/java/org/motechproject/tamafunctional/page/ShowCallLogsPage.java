package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowCallLogsPage extends Page {

    @FindBy(how = How.ID, using = "_title_title[0]_id")
    private WebElement utilPanel;

    public ShowCallLogsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        utilPanel = WebDriverFactory.createWebElement(utilPanel);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("_title_title[0]_id");
    }

}
