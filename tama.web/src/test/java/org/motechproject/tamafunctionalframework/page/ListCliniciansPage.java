package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ListCliniciansPage extends Page {

    final static public String FIRST_CLINICIAN_UTILBOX_XPATH = "//a[@title='Show Clinician']";

    @FindBy(how = How.XPATH, using = FIRST_CLINICIAN_UTILBOX_XPATH)
    private WebElement firstClinician;


    public ListCliniciansPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithXPATHToLoad(FIRST_CLINICIAN_UTILBOX_XPATH);
    }


    public ShowClinicianPage goToShowClinicianPage() {
        firstClinician.click();
        return MyPageFactory.initElements(webDriver, ShowClinicianPage.class);
    }
}