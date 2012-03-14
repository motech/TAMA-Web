package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ReactivatePatientPage extends Page{
    public static final String REACTIVATE_PATIENT_ID = "reactivatePatient";

    @FindBy(how = How.ID, using = REACTIVATE_PATIENT_ID)
    private WebElement reactivatePatientButton;

    public ReactivatePatientPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(REACTIVATE_PATIENT_ID);
    }

    public ShowPatientPage backfillAdherenceAsTakenByDefault(){
        this.reactivatePatientButton.click();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

}