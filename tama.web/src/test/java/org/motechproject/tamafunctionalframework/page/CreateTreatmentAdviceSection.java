package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.ExtendedWebElement;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreateTreatmentAdviceSection {

    public static final String REGIMEN_ID = "_treatmentAdvice.regimenId_id";

    public static final String DRUG_BRAND1_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_drugName1_drugName_id";
    public static final String TREATMENT_ADVICE_DRUG_DOSAGES_0_MORNING_TIME_ID = "_treatmentAdvice.drugDosages[0].morningTime_id";
    public static final String TREATMENT_ADVICE_DRUG_DOSAGES_0_EVENING_TIME_ID = "_treatmentAdvice.drugDosages[0].eveningTime_id";
    public static final String TREATMENT_ADVICE_DRUG_DOSAGES_1_MORNING_TIME_ID = "_treatmentAdvice.drugDosages[1].morningTime_id";
    public static final String TREATMENT_ADVICE_DRUG_DOSAGES_1_EVENING_TIME_ID = "_treatmentAdvice.drugDosages[1].eveningTime_id";

    @FindBy(how = How.ID, using = REGIMEN_ID)
    private WebElement regimenElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionGroupId_id")
    private WebElement drugCompositionGroupElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].dosageTypeId_id")
    private WebElement drug1DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].startDateAsDate_id")
    private WebElement drug1StartDateElement;

    @FindBy(how = How.ID, using = TREATMENT_ADVICE_DRUG_DOSAGES_0_MORNING_TIME_ID)
    private WebElement drug1MorningDosageTimeElement;

    @FindBy(how = How.ID, using = TREATMENT_ADVICE_DRUG_DOSAGES_0_EVENING_TIME_ID)
    private WebElement drug1EveningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].mealAdviceId_id")
    private WebElement drug1MealAdviceTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].dosageTypeId_id")
    private WebElement drug2DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].startDateAsDate_id")
    private WebElement drug2StartDateElement;

    @FindBy(how = How.ID, using = TREATMENT_ADVICE_DRUG_DOSAGES_1_MORNING_TIME_ID)
    private WebElement drug2MorningDosageTimeElement;

    @FindBy(how = How.ID, using = TREATMENT_ADVICE_DRUG_DOSAGES_1_EVENING_TIME_ID)
    private WebElement drug2EveningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].mealAdviceId_id")
    private WebElement drug2MealAdviceTypeElement;

    public void postInitialize() {
        regimenElement = WebDriverFactory.createWebElement(regimenElement);
        drugCompositionGroupElement = WebDriverFactory.createWebElement(drugCompositionGroupElement);
        drug1DosageTypeElement = WebDriverFactory.createWebElement(drug1DosageTypeElement);
        drug1StartDateElement = WebDriverFactory.createWebElement(drug1StartDateElement);
        drug1MorningDosageTimeElement = WebDriverFactory.createWebElement(drug1MorningDosageTimeElement);
        drug1EveningDosageTimeElement = WebDriverFactory.createWebElement(drug1EveningDosageTimeElement);
        drug1MealAdviceTypeElement = WebDriverFactory.createWebElement(drug1MealAdviceTypeElement);
        drug2DosageTypeElement = WebDriverFactory.createWebElement(drug2DosageTypeElement);
        drug2StartDateElement = WebDriverFactory.createWebElement(drug2StartDateElement);
        drug2MorningDosageTimeElement = WebDriverFactory.createWebElement(drug2MorningDosageTimeElement);
        drug2EveningDosageTimeElement = WebDriverFactory.createWebElement(drug2EveningDosageTimeElement);
        drug2MealAdviceTypeElement = WebDriverFactory.createWebElement(drug2MealAdviceTypeElement);
    }

    protected void waitForPageToLoad(Page page) {
        page.waitForDojoElementToLoad(REGIMEN_ID, "dijitInputInner");
    }

    protected void fillRegimenSection(TestTreatmentAdvice treatmentAdvice, Page page) {
        TestDrugDosage testDrugDosage1 = treatmentAdvice.drugDosages().get(0);
        TestDrugDosage testDrugDosage2 = treatmentAdvice.drugDosages().get(1);
        String dosageType = testDrugDosage1.dosageType();
        createFirstDosage(testDrugDosage1, dosageType, page);
        createSecondDosage(testDrugDosage2, dosageType, page);
    }

    protected void submit() {
        drug1StartDateElement.submit();
    }

    private void createFirstDosage(TestDrugDosage testDrugDosage1, String dosageType, Page page) {
        logDosage(testDrugDosage1, page);
        ((ExtendedWebElement) drug1DosageTypeElement).select(dosageType);
        ((ExtendedWebElement) drug1StartDateElement).select(testDrugDosage1.startDate());
        if (testDrugDosage1.isMorningDosage()) {
            page.waitForElementWithIdToLoad(TREATMENT_ADVICE_DRUG_DOSAGES_0_MORNING_TIME_ID);
            drug1MorningDosageTimeElement.sendKeys(testDrugDosage1.dosageSchedule());
        } else {
            page.waitForElementWithIdToLoad(TREATMENT_ADVICE_DRUG_DOSAGES_0_EVENING_TIME_ID);
            drug1EveningDosageTimeElement.sendKeys(testDrugDosage1.dosageSchedule());
        }
        drug1MealAdviceTypeElement.sendKeys(testDrugDosage1.mealAdvice());
    }

    private void createSecondDosage(TestDrugDosage testDrugDosage2, String dosageType, Page page) {
        logDosage(testDrugDosage2, page);
        ((ExtendedWebElement) drug2DosageTypeElement).select(dosageType);
        ((ExtendedWebElement) drug2StartDateElement).select(testDrugDosage2.startDate());
        if (testDrugDosage2.isMorningDosage()) {
            page.waitForElementWithIdToLoad(TREATMENT_ADVICE_DRUG_DOSAGES_1_MORNING_TIME_ID);
            drug2MorningDosageTimeElement.sendKeys(testDrugDosage2.dosageSchedule());
        } else {
            page.waitForElementWithIdToLoad(TREATMENT_ADVICE_DRUG_DOSAGES_1_EVENING_TIME_ID);
            drug2EveningDosageTimeElement.sendKeys(testDrugDosage2.dosageSchedule());
        }
        drug2MealAdviceTypeElement.sendKeys(testDrugDosage2.mealAdvice());
    }

    private void logDosage(TestDrugDosage drugDosage, Page page) {
        page.logInfo("%s Dosage at %s", drugDosage.dosageType(), drugDosage.dosageSchedule());
    }

}
