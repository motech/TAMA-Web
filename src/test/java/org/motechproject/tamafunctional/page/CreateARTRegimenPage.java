package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.ExtendedWebElement;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class CreateARTRegimenPage extends Page {

    public static final String REGIMEN_ID = "_treatmentAdvice.regimenId_id";
    public static final String DISCONTINUATION_REASON_ID = "_discontinuationReason_id";

    public static final String DRUG1_BRAND_ID = "_treatmentAdvice.drugDosages[0].brandId_id";
    public static final String DRUG_BRAND1_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_drugName1_drugName_id";

    @FindBy(how = How.ID, using = REGIMEN_ID)
    private WebElement regimenElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionGroupId_id")
    private WebElement drugCompositionGroupElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionId_id")
    private WebElement drugCompositionElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].dosageTypeId_id")
    private WebElement drug1DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].eveningTime_id")
    private WebElement drug1EveningDosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].mealAdviceId_id")
    private WebElement drug1MealAdviceTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].dosageTypeId_id")
    private WebElement drug2DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].e_id")
    private WebElement drug2DosageTimeElement;

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
        drug1EveningDosageTimeElement = WebDriverFactory.createWebElement(drug1EveningDosageTimeElement);
        drug1MealAdviceTypeElement = WebDriverFactory.createWebElement(drug1MealAdviceTypeElement);
        drug2DosageTypeElement = WebDriverFactory.createWebElement(drug2DosageTypeElement);
        drug2DosageTimeElement = WebDriverFactory.createWebElement(drug2DosageTimeElement);
        drug2MealAdviceTypeElement = WebDriverFactory.createWebElement(drug2MealAdviceTypeElement);

        discontinuationReasonElement = WebDriverFactory.createWebElement(discontinuationReasonElement);
        nextToRegisterNewTreatmentAdvice = WebDriverFactory.createWebElement(nextToRegisterNewTreatmentAdvice);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad(REGIMEN_ID,"dijitInputInner");
    }

    public ShowPatientPage registerNewARTRegimen(TestTreatmentAdvice treatmentAdvice) {
//        These calls are problematic because of ajax based implementation, using the default composition and regimen instead
//        selectRegimenAndWaitTillTheCompositionGroupsShow(treatmentAdvice);
//        selectDrugCompositionAndWaitTillTheDrugDosagesShow(treatmentAdvice);

        setupNewARTRegimen(treatmentAdvice);
        this.waitForElementWithIdToLoad(ShowPatientPage.PATIENT_ID_ID);
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public ViewARTRegimenPage reCreateARTRegimen(TestTreatmentAdvice treatmentAdvice) {
        discontinuationReasonElement.sendKeys(treatmentAdvice.discontinuationReason());
        nextToRegisterNewTreatmentAdvice.click();
        setupNewARTRegimen(treatmentAdvice);
        this.waitForElementWithIdToLoad(ViewARTRegimenPage.REGIMEN_TEXT_ID);
        return MyPageFactory.initElements(webDriver, ViewARTRegimenPage.class);
    }

    private void setupNewARTRegimen(TestTreatmentAdvice treatmentAdvice) {
        TestDrugDosage drugDosage1 = treatmentAdvice.drugDosages().get(0);
        drug1DosageTypeElement.sendKeys(drugDosage1.dosageType());
        tabOut(drug1DosageTypeElement);
        drug1EveningDosageTimeElement.sendKeys(drugDosage1.dosageSchedule());
        drug1MealAdviceTypeElement.sendKeys(drugDosage1.mealAdvice());

        TestDrugDosage drugDosage2 = treatmentAdvice.drugDosages().get(1);
        drug2DosageTypeElement.sendKeys(drugDosage2.dosageType());
        tabOut(drug2DosageTypeElement);
        drug2DosageTimeElement.sendKeys(drugDosage2.dosageSchedule());
        drug2MealAdviceTypeElement.sendKeys(drugDosage2.mealAdvice());

        saveElement.click();
    }

    private void tabOut(WebElement webElement) {
        ((ExtendedWebElement)webElement).sendKey(Keys.TAB);
    }

    private void selectRegimenAndWaitTillTheCompositionGroupsShow(TestTreatmentAdvice treatmentAdvice) {
        regimenElement.sendKeys(treatmentAdvice.regimenName());
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(CreateARTRegimenPage.DRUG1_BRAND_ID)) != null;
            }
        });
    }

    private void selectDrugCompositionAndWaitTillTheDrugDosagesShow(TestTreatmentAdvice treatmentAdvice) {
        drugCompositionGroupElement.sendKeys(treatmentAdvice.drugCompositionName());
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(CreateARTRegimenPage.DRUG1_BRAND_ID)) != null;
            }
        });
    }
}
