package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowTreatmentAdviceSection {

    private static final String REGIMEN_TEXT_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_regimens_regimenName_id";
    public static final String PAGE_LOAD_MARKER = REGIMEN_TEXT_ID;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_TreatmentAdvice_patientId_patientId_id")
    private WebElement patientIdElement;

    @FindBy(how = How.ID, using = REGIMEN_TEXT_ID)
    private WebElement regimenNameElement;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_TreatmentAdvice_regimencompositions_drugCompositionName_id")
    private WebElement drugCompositionGroupNameElement;

    @FindBy(how = How.ID, using = "changeRegimen")
    private WebElement changeRegimenElement;

    public String getRegimenName() {
        return regimenNameElement.getText();
    }

    public String getDrugCompositionGroupName() {
        return drugCompositionGroupNameElement.getText();
    }

    public void clickChangeRegimen() {
        changeRegimenElement.click();
    }

    public TestTreatmentAdvice getTreatmentAdvice() {
        return new TestTreatmentAdvice().regimenName(getRegimenName()).drugCompositionName(getDrugCompositionGroupName());
    }
}
