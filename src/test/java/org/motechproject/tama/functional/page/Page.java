package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class Page {
    protected WebDriver webDriver;

    public Page(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @FindBy(how = How.ID, using = "patientId")
    private WebElement searchBox;

    @FindBy(how = How.ID, using = "searchPatient")
    private WebElement searchButton;

    @FindBy(how = How.ID, using = "patientSearchError")
    private WebElement errorDiv;

    public ShowPatientPage searchPatientBy(String id) {
        searchBox.sendKeys(id);
        searchButton.click();
            return PageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public Page unsuccesfulSearchPatientBy(String id, Class<? extends Page> returnPage) {
        searchBox.sendKeys(id);
        searchButton.click();
        return PageFactory.initElements(webDriver, returnPage);
    }

    public String getPatientSearchErrorMessage() {
        return errorDiv.getText();
    }
}
