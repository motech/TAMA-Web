package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ListPatientsPage {
    private WebDriver webDriver;

    @FindBy(how = How.ID, using = "patientId")
    private WebElement searchBox;

    @FindBy(how = How.ID, using = "searchPatient")
    private WebElement searchButton;

    public ListPatientsPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public ShowPatientPage searchPatientBy(String id) {
        searchBox.sendKeys(id);
        searchButton.click();
        return PageFactory.initElements(webDriver, ShowPatientPage.class);
    }

}
