package org.motechproject.tama.functional.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Page {
    protected WebDriver webDriver;
    private static final long MaxPageLoadTime = 60;
    protected WebDriverWait wait;

    public Page(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.wait = new WebDriverWait(webDriver, MaxPageLoadTime);
    }

    @FindBy(how = How.ID, using = "patientId")
    private WebElement searchBox;

    @FindBy(how = How.ID, using = "searchPatient")
    private WebElement searchButton;

    @FindBy(how = How.ID, using = "patientSearchError")
    private WebElement errorDiv;

    public ShowPatientPage searchPatientBy(String id) {
        searchById(id);
        this.waitForElementWithIdToLoad(ShowPatientPage.PATIENT_ID_ID);
        return PageFactory.initElements(webDriver, ShowPatientPage.class);
    }


    public Page unsuccessfulSearchPatientBy(String id, Class<? extends Page> returnPageClass, String idOnTheReturnPage) {
        searchById(id);
        this.waitForElementWithIdToLoad(idOnTheReturnPage);
        return PageFactory.initElements(webDriver, returnPageClass);
    }

    private void searchById(String id) {
        searchBox.sendKeys(id);
        searchButton.click();
    }

    public String getPatientSearchErrorMessage() {
        return errorDiv.getText();
    }

    public void postInitialize() {
    }

    protected void waitForElementWithIdToLoad(final String id) {
        waitForElementToLoad(By.id(id));
    }

    protected void waitForElementWithXPATHToLoad(final String path) {
        waitForElementToLoad(By.xpath(path));
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
