package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.FunctionalTestObject;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public abstract class Page extends FunctionalTestObject {
    protected WebDriver webDriver;
    private static final long MaxPageLoadTime = 30;
    private static final long RetryTimes = 5;
    private static final long RetryInterval = 5;
    protected WebDriverWait wait;
    protected WebDriverWait waitWithRetry;

    public Page(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, MaxPageLoadTime);
        this.waitWithRetry = new WebDriverWait(webDriver, RetryInterval);
        this.waitForPageToLoad();
    }

    @FindBy(how = How.ID, using = "patientId")
    private WebElement searchBox;

    @FindBy(how = How.ID, using = "searchPatient")
    private WebElement searchButton;

    @FindBy(how = How.ID, using = "patientSearchError")
    private WebElement errorDiv;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private WebElement logoutLink;

    public ShowPatientSummaryPage searchPatientBy(String id) {
        searchById(id);
        this.waitForElementWithIdToLoad(ShowPatientSummaryPage.PATIENT_ID_ID);
        return MyPageFactory.initElements(webDriver, ShowPatientSummaryPage.class);
    }

    public Page unsuccessfulSearchPatientBy(String id, Class<? extends Page> returnPageClass, String idOnTheReturnPage) {
        searchById(id);
        this.waitForElementWithIdToLoad(idOnTheReturnPage);
        return MyPageFactory.initElements(webDriver, returnPageClass);
    }

    private void searchById(String id) {
        searchBox = WebDriverFactory.createWebElement(searchBox);
        searchBox.sendKeys(id);
        searchBox.submit();
    }

    public String getPatientSearchErrorMessage() {
        return errorDiv.getText();
    }

    public WebElement getNavigationLinks() {
        waitForElementWithIdToLoad("links");
        return webDriver.findElement(By.id("links"));
    }

    protected abstract void waitForPageToLoad();

    public void postInitialize() {
    }

    public void logout() {
        logoutLink.click();
    }

    @Override
    public void logInfo(String message, String... params) {
        super.logInfo(message, params);
    }

    protected void waitForElementWithIdToLoad(final String id) {
        waitForElementToLoad(By.id(id));
    }

    protected void waitForElementWithXPATHToLoad(final String path) {
        waitForElementToLoad(By.xpath(path));
    }

    protected void waitForDojoElementToLoad(final String id, final String dojoClass) {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try {
                    WebElement element = webDriver.findElement(By.id(id));
                    return element != null && isNotBlank(element.getAttribute("class")) && element.getAttribute("class").contains(dojoClass);
                } catch (StaleElementReferenceException ex) {
                    return false;
                }
            }
        });
    }

    protected void waitForElementToLoadWithRetry(final By by) {
        for (int i = 1; i <= RetryTimes; i++) {
            try {
                Boolean foundElement = waitWithRetry.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        return webDriver.findElement(by) != null;
                    }
                });
                if (foundElement) break;
            }
            catch (TimeoutException e) {
                logInfo(String.format("Retried %s time(s) ...", i));
                if (i == RetryTimes)
                    throw e;
            }
            webDriver.get(webDriver.getCurrentUrl());
        }
    }

    private void waitForElementToLoad(final By by) {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(by) != null;
            }
        });
    }
}
