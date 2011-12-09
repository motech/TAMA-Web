package org.motechproject.tamafunctional.page;


import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestPatient;
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
    @FindBy(how = How.ID, using = "c_org_motechproject_tama_domain_Patient_medicalHistory_question_1")
    private WebElement baselineHb;

    public void postInitialize() {
        hivTestReason = WebDriverFactory.createWebElement(hivTestReason);
        modeOfTransmission = WebDriverFactory.createWebElement(modeOfTransmission);
        arvAllergy = WebDriverFactory.createWebElement(arvAllergy);
        arvAllergyDescription = WebDriverFactory.createWebElement(arvAllergyDescription);
        drdRash = WebDriverFactory.createWebElement(drdRash);
        nextToPatientPreferences = WebDriverFactory.createWebElement(nextToPatientPreferences);
        baselineHb = WebDriverFactory.createWebElement(baselineHb);
    }

    public void enterDetails(TestPatient patient) {
        ((ExtendedWebElement)hivTestReason).select(patient.hivMedicalHistory().testReason());
        ((ExtendedWebElement)modeOfTransmission).select(patient.hivMedicalHistory().modeOfTransmission());
        arvAllergy.click();
        arvAllergyDescription.sendKeys("arvAllergyDescription");
        drdRash.click();
        if(patient.nonHIVMedicalHistory().isBaseLinePreTherapyLowerThanTen())
            baselineHb.click();

        nextToPatientPreferences.click();
    }
}
