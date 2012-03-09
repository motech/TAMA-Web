package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.testdata.TestOpportunisticInfections;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowOpportunisticInfectionsSection {

    @FindBy(how = How.ID, using = "opportunisticInfection1")
    private WebElement anemia;

    @FindBy(how = How.ID, using = "opportunisticInfection7")
    private WebElement hypertension;

    @FindBy(how = How.ID, using = "opportunisticInfection9")
    private WebElement malaria;

    @FindBy(how = How.ID, using = "opportunisticInfection15")
    private WebElement other;


    @FindBy(how = How.ID, using = "oi_edit_link")
    private WebElement editLink;

    public boolean getAnemia() {
        return anemia.isSelected();
    }

    public boolean getHypertension() {
        return hypertension!= null && hypertension.isSelected();
    }

    public boolean getMalaria() {
        return malaria != null && malaria.isSelected();
    }

    public boolean getOther() {
        return other.isSelected();
    }

    public String getOtherDetails() {
        if(!elementExists(other))
            return "";
        String otherText = other.getText();
        String label = "Other: ";
        return otherText.substring(label.length(), otherText.length());
    }

    public void clickEdit() {
        this.editLink.click();
    }

    public TestOpportunisticInfections getOpportunisticInfections() {
        
        return new TestOpportunisticInfections()
                .setAnemia(elementExists(anemia))
                .setHypertension(elementExists(hypertension))
                .setMalaria(elementExists(malaria))
                .setOther(elementExists(other))
                .setOtherDetails(getOtherDetails());
    }

    private boolean elementExists(WebElement element) {
        try{
            return element.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
