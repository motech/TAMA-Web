package org.motechproject.tamafunctional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ViewARTRegimenPage extends Page {

    public static final String REGIMEN_TEXT_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_regimens_regimenName_id";

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_TreatmentAdvice_patientId_patientId_id")
    private WebElement patientIdElement;

    @FindBy(how = How.ID, using = REGIMEN_TEXT_ID)
    private WebElement regimenNameElement;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_TreatmentAdvice_regimencompositions_regimenCompositionName_id")
    private WebElement regimenCompositionNameElement;

    public ViewARTRegimenPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(REGIMEN_TEXT_ID);
    }

    public String getPatientId() {
        return patientIdElement.getText();
    }

    public String getRegimenName() {
        return regimenNameElement.getText();
    }

    public String getRegimenCompositionName() {
        return regimenCompositionNameElement.getText();
    }
}
