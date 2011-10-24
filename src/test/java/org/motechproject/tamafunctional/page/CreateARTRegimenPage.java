package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreateARTRegimenPage extends Page {

    public static final String REGIMEN_ID = "_treatmentAdvice.regimenId_id";
    public static final String DISCONTINUATION_REASON_ID = "_discontinuationReason_id";

    public static final String DRUG_BRAND1_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_drugName1_drugName_id";

    @FindBy(how = How.ID, using = REGIMEN_ID)
    private WebElement regimenElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionGroupId_id")
    private WebElement drugCompositionGroupElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].dosageTypeId_id")
    private WebElement drug1DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].morningTime_id")
    private WebElement drug1MorningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].eveningTime_id")
    private WebElement drug1EveningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].mealAdviceId_id")
    private WebElement drug1MealAdviceTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].dosageTypeId_id")
    private WebElement drug2DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].morningTime_id")
    private WebElement drug2MorningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].eveningTime_id")
    private WebElement drug2EveningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].mealAdviceId_id")
    private WebElement drug2MealAdviceTypeElement;

    @FindBy(how = How.ID, using = DISCONTINUATION_REASON_ID)
    private WebElement discontinuationReasonElement;

    @FindBy(how = How.ID, using = "nextToRegisterNewTreatmentAdvice")
    private WebElement nextToRegisterNewTreatmentAdvice;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement saveElement;

    public CreateARTRegimenPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        regimenElement = WebDriverFactory.createWebElement(regimenElement);
        drugCompositionGroupElement = WebDriverFactory.createWebElement(drugCompositionGroupElement);
        drug1DosageTypeElement = WebDriverFactory.createWebElement(drug1DosageTypeElement);
        drug1MorningDosageTimeElement = WebDriverFactory.createWebElement(drug1MorningDosageTimeElement);
        drug1EveningDosageTimeElement = WebDriverFactory.createWebElement(drug1EveningDosageTimeElement);
        drug1MealAdviceTypeElement = WebDriverFactory.createWebElement(drug1MealAdviceTypeElement);
        drug2DosageTypeElement = WebDriverFactory.createWebElement(drug2DosageTypeElement);
        drug2MorningDosageTimeElement = WebDriverFactory.createWebElement(drug2MorningDosageTimeElement);
        drug2EveningDosageTimeElement = WebDriverFactory.createWebElement(drug2EveningDosageTimeElement);
        drug2MealAdviceTypeElement = WebDriverFactory.createWebElement(drug2MealAdviceTypeElement);

        discontinuationReasonElement = WebDriverFactory.createWebElement(discontinuationReasonElement);
        nextToRegisterNewTreatmentAdvice = WebDriverFactory.createWebElement(nextToRegisterNewTreatmentAdvice);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad(REGIMEN_ID, "dijitInputInner");
    }

    public ShowPatientPage registerNewARTRegimen(TestTreatmentAdvice treatmentAdvice) {
        setupNewARTRegimen(treatmentAdvice);
        this.waitForElementWithIdToLoad(ShowPatientPage.PATIENT_ID_ID);
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public ShowARTRegimenPage reCreateARTRegimen(TestTreatmentAdvice treatmentAdvice) {
        discontinuationReasonElement.sendKeys(treatmentAdvice.discontinuationReason());
        nextToRegisterNewTreatmentAdvice.click();
        setupNewARTRegimen(treatmentAdvice);
        this.waitForElementWithIdToLoad(ShowARTRegimenPage.REGIMEN_TEXT_ID);
        return MyPageFactory.initElements(webDriver, ShowARTRegimenPage.class);
    }

    private void setupNewARTRegimen(TestTreatmentAdvice treatmentAdvice) {
        TestDrugDosage drugDosage1 = treatmentAdvice.drugDosages().get(0);
        logDosage(drugDosage1);
        ((ExtendedWebElement) drug1DosageTypeElement).select(drugDosage1.dosageType());
        drug1MealAdviceTypeElement.click();
        if (drugDosage1.isMorningDosage())
            drug1MorningDosageTimeElement.sendKeys(drugDosage1.dosageSchedule());
        else
            drug1EveningDosageTimeElement.sendKeys(drugDosage1.dosageSchedule());
        drug1MealAdviceTypeElement.sendKeys(drugDosage1.mealAdvice());

        TestDrugDosage drugDosage2 = treatmentAdvice.drugDosages().get(1);
        logDosage(drugDosage2);
        ((ExtendedWebElement) drug2DosageTypeElement).select(drugDosage2.dosageType());
        drug2MealAdviceTypeElement.click();
        if (drugDosage2.isMorningDosage())
            drug2MorningDosageTimeElement.sendKeys(drugDosage2.dosageSchedule());
        else
            drug2EveningDosageTimeElement.sendKeys(drugDosage2.dosageSchedule());
        drug2MealAdviceTypeElement.sendKeys(drugDosage2.mealAdvice());

        saveElement.click();
    }

    private void logDosage(TestDrugDosage drugDosage) {
        logInfo("%s Dosage at %s", drugDosage.dosageType(), drugDosage.dosageSchedule());
    }
}
