package org.motechproject.tamafunctional.page;


import org.motechproject.tamafunctional.setup.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreatePatientPreferencesSection {

    @FindBy(how = How.ID, using = "_ivrLanguage_id")
    private WebElement ivrLanguage;
    @FindBy(how = How.ID, using = "_passcode_id")
    private WebElement passcode;

    public void postInitialize() {
        ivrLanguage = WebDriverFactory.createWebElement(ivrLanguage);
        passcode = WebDriverFactory.createWebElement(passcode);
    }

    public void enterDetails(TestPatient patient) {
        passcode.clear();
        passcode.sendKeys(String.valueOf(patient.passcode()));
    }
}
