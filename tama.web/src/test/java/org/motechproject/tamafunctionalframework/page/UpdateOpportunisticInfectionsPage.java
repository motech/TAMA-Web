package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestOpportunisticInfections;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class UpdateOpportunisticInfectionsPage extends Page {

    public static final String PAGE_LOAD_MARKER = "page_load_marker";

    @FindBy(how = How.ID, using = "opportunisticInfection1")
    private WebElement anemia;

    @FindBy(how = How.ID, using = "opportunisticInfection7")
    private WebElement hypertension;

    @FindBy(how = How.ID, using = "opportunisticInfection9")
    private WebElement malaria;

    @FindBy(how = How.ID, using = "otherOpportunisticInfection")
    private WebElement other;

    @FindBy(how = How.ID, using = "_otherDetails_id")
    private WebElement otherDetails;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement saveButton;

    public UpdateOpportunisticInfectionsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PAGE_LOAD_MARKER);
    }

    public void postInitialize() {
        anemia = WebDriverFactory.createWebElement(anemia);
        hypertension = WebDriverFactory.createWebElement(hypertension);
        malaria = WebDriverFactory.createWebElement(malaria);
        other = WebDriverFactory.createWebElement(other);
        otherDetails = WebDriverFactory.createWebElement(otherDetails);
        saveButton = WebDriverFactory.createWebElement(saveButton);
    }

    public ShowClinicVisitPage enterOpportunisticInfectionsAndSave(TestOpportunisticInfections opportunisticInfections) {

        update(anemia, opportunisticInfections.isAnemia());
        update(hypertension, opportunisticInfections.isHypertension());
        update(malaria, opportunisticInfections.isMalaria());

        if(opportunisticInfections.isOther()) {
            if(other.isSelected()) {
                otherDetails.clear();
                otherDetails.sendKeys(opportunisticInfections.getOtherDetails());
            }
        } else {
            if(other.isSelected()) {
                otherDetails.clear();
                other.click();
            }
        }

        saveButton.click();
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }

    private void update(WebElement anemia, boolean setSelected) {
        if(setSelected) select(anemia);
        else unselect(anemia);
    }

    private void select(WebElement anemia) {
        if(anemia.isSelected()) return;
        anemia.click();
    }

    private void unselect(WebElement anemia) {
        if(!anemia.isSelected()) return;
        anemia.click();
    }

}
