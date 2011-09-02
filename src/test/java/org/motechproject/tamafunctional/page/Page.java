package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public abstract class Page {
    protected WebDriver webDriver;
    private static final long MaxPageLoadTime = 10;
    protected WebDriverWait wait;

    public Page(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, MaxPageLoadTime);

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

    public ShowPatientPage searchPatientBy(String id) {
        searchById(id);
        this.waitForElementWithIdToLoad(ShowPatientPage.PATIENT_ID_ID);
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public Page unsuccessfulSearchPatientBy(String id, Class<? extends Page> returnPageClass, String idOnTheReturnPage) {
        searchById(id);
        this.waitForElementWithIdToLoad(idOnTheReturnPage);
        return MyPageFactory.initElements(webDriver, returnPageClass);
    }

    private void searchById(String id) {
        searchBox = WebDriverFactory.createWebElement(searchBox);
        searchBox.sendKeys(id);
        searchButton.click();
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
                WebElement element = webDriver.findElement(By.id(id));
                return element != null && isNotBlank(element.getAttribute("class")) && element.getAttribute("class").contains(dojoClass);
            }
        });
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
