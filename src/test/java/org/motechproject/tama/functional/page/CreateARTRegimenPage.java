package org.motechproject.tama.functional.page;

import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebElement;
import org.motechproject.tama.web.model.DrugDosageView;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class CreateARTRegimenPage extends Page {

    public static final String REGIMEN_ID = "_regimenId_id";

    public static final String DRUG1_BRAND_ID = "_treatmentAdvice.drugDosages[0].brandId_id";

    @FindBy(how = How.ID, using = REGIMEN_ID)
    private WebElement regimenElement;

    @FindBy(how = How.ID, using = "_regimenCompositionId_id")
    private WebElement regimenCompositionElement;

    @FindBy(how = How.ID, using = DRUG1_BRAND_ID)
    private WebElement drug1BrandElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].dosageTypeId_id")
    private WebElement drug1DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].dosageSchedules[0]_id")
    private WebElement drug1DosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[0].mealAdviceId_id")
    private WebElement drug1MealAdviceTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].brandId_id")
    private WebElement drug2BrandElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].dosageTypeId_id")
    private WebElement drug2DosageTypeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].dosageSchedules[0]_id")
    private WebElement drug2DosageTimeElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugDosages[1].mealAdviceId_id")
    private WebElement drug2MealAdviceTypeElement;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement saveElement;

    public CreateARTRegimenPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        regimenElement = new MyWebElement(regimenElement);
        regimenCompositionElement = new MyWebElement(regimenCompositionElement);
        drug1BrandElement = new MyWebElement(drug1BrandElement);
        drug1DosageTypeElement = new MyWebElement(drug1DosageTypeElement);
        drug1DosageTimeElement = new MyWebElement(drug1DosageTimeElement);
        drug1MealAdviceTypeElement = new MyWebElement(drug1MealAdviceTypeElement);
        drug2BrandElement = new MyWebElement(drug2BrandElement);
        drug2DosageTypeElement = new MyWebElement(drug2DosageTypeElement);
        drug2DosageTimeElement = new MyWebElement(drug2DosageTimeElement);
        drug2MealAdviceTypeElement = new MyWebElement(drug2MealAdviceTypeElement);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(REGIMEN_ID);
    }

    public ShowPatientPage registerNewARTRegimen(TreatmentAdviceView treatmentAdvice) {
        selectRegimenAndWaitTillTheCompositionsShow(treatmentAdvice);
        selectDrugCompositionAndWaitTillTheDrugDosagesShow(treatmentAdvice);

        DrugDosageView drugDosage1 = treatmentAdvice.getDrugDosages().get(0);
        drug1BrandElement.sendKeys(drugDosage1.getBrandName());
        drug1DosageTypeElement.sendKeys(drugDosage1.getDosageType());
        drug1DosageTimeElement.sendKeys(drugDosage1.getDosageSchedules().get(0));
        drug1MealAdviceTypeElement.sendKeys(drugDosage1.getMealAdviceType());

        DrugDosageView drugDosage2 = treatmentAdvice.getDrugDosages().get(1);
        drug2BrandElement.sendKeys(drugDosage2.getBrandName());
        drug2DosageTypeElement.sendKeys(drugDosage2.getDosageType());
        drug2DosageTimeElement.sendKeys(drugDosage2.getDosageSchedules().get(0));
        drug2MealAdviceTypeElement.sendKeys(drugDosage2.getMealAdviceType());

        saveElement.click();
        this.waitForElementWithIdToLoad(ShowPatientPage.PATIENT_ID_ID);
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    private void selectRegimenAndWaitTillTheCompositionsShow(TreatmentAdviceView treatmentAdvice) {
        regimenElement.sendKeys(treatmentAdvice.getRegimenName());
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(CreateARTRegimenPage.DRUG1_BRAND_ID)) != null;
            }
        });
    }

    private void selectDrugCompositionAndWaitTillTheDrugDosagesShow(TreatmentAdviceView treatmentAdvice) {
        regimenCompositionElement.sendKeys(treatmentAdvice.getRegimenCompositionName());
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(CreateARTRegimenPage.DRUG1_BRAND_ID)) != null;
            }
        });
    }
}
