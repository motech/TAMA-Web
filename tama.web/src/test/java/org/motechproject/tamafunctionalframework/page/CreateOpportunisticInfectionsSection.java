package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestOpportunisticInfections;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreateOpportunisticInfectionsSection {

    public static final String PAGE_LOAD_MARKER = "page_load_marker";

    @FindBy(how = How.ID, using = "opportunisticInfection1")
    private WebElement anemia;

    @FindBy(how = How.ID, using = "opportunisticInfection7")
    private WebElement hypertension;

    @FindBy(how = How.ID, using = "opportunisticInfection9")
    private WebElement malaria;

    @FindBy(how = How.ID, using = "otherOpportunisticInfection")
    private WebElement other;

    @FindBy(how = How.ID, using = "_opportunisticInfectionsUIModel.otherDetails_id")
    private WebElement otherDetails;

    public void postInitialize() {
        anemia = WebDriverFactory.createWebElement(anemia);
        hypertension = WebDriverFactory.createWebElement(hypertension);
        malaria = WebDriverFactory.createWebElement(malaria);
        other = WebDriverFactory.createWebElement(other);
        otherDetails = WebDriverFactory.createWebElement(otherDetails);
    }

    public void fillOpportunisticInfections(TestOpportunisticInfections opportunisticInfections) {
        if(opportunisticInfections.isAnemia()) anemia.click();
        if(opportunisticInfections.isHypertension()) hypertension.click();
        if(opportunisticInfections.isMalaria()) malaria.click();
        if(opportunisticInfections.isOther()) {
            other.click();
            otherDetails.clear();
            otherDetails.sendKeys(opportunisticInfections.getOtherDetails());
        }
    }
}
