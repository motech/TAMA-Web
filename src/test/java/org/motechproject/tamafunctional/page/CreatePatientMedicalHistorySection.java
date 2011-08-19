package org.motechproject.tamafunctional.page;


import org.motechproject.tama.domain.HIVMedicalHistory;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreatePatientMedicalHistorySection {

    @FindBy(how = How.ID, using = "_medicalHistory.hivMedicalHistory.testReasonId_id")
    private WebElement hivTestReason;
    @FindBy(how = How.ID, using = "_medicalHistory.hivMedicalHistory.modeOfTransmissionId_id")
    private WebElement modeOfTransmission;
    @FindBy(how = How.ID, using = "c_org_motechproject_tama_domain_Patient_allergy1")
    private WebElement arvAllergy;
    @FindBy(how = How.ID, using = "_medicalHistory.nonHivMedicalHistory.allergiesHistory[1].description_id")
    private WebElement arvAllergyDescription;
    @FindBy(how = How.ID, using = "c_org_motechproject_tama_domain_Patient_rash_0")
    private WebElement drdRash;
    @FindBy(how = How.ID, using = "nextToPatientPreferences")
    private WebElement nextToPatientPreferences;

    public void postInitialize() {
        hivTestReason = WebDriverFactory.createWebElement(hivTestReason);
        modeOfTransmission = WebDriverFactory.createWebElement(modeOfTransmission);
        arvAllergy = WebDriverFactory.createWebElement(arvAllergy);
        arvAllergyDescription = WebDriverFactory.createWebElement(arvAllergyDescription);
        drdRash = WebDriverFactory.createWebElement(drdRash);
        nextToPatientPreferences = WebDriverFactory.createWebElement(nextToPatientPreferences);
    }

    public void enterDetails(Patient patient) {
        HIVMedicalHistory hivMedicalHistory = patient.getMedicalHistory().getHivMedicalHistory();
        hivTestReason.sendKeys(String.valueOf(hivMedicalHistory.getTestReason().getName()));
        modeOfTransmission.sendKeys(String.valueOf(hivMedicalHistory.getModeOfTransmission().getType()));
        arvAllergy.click();
        arvAllergyDescription.sendKeys("arvAllergyDescription");
        drdRash.click();

        nextToPatientPreferences.click();
    }
}
