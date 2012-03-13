package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.ExtendedWebElement;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.Map;

public class CreateTreatmentAdviceSection extends Page {

    public static final String REGIMEN_ID = "_treatmentAdvice.regimenId_id";

    public static final String DRUG_BRAND1_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_drugName1_drugName_id";

    @FindBy(how = How.ID, using = REGIMEN_ID)
    private WebElement regimenElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionGroupId_id")
    private WebElement drugCompositionGroupElement;

    private DrugDosageSection drug1Section, drug2Section;

    public CreateTreatmentAdviceSection(WebDriver webDriver) {
        super(webDriver);
        drug1Section = new Drug1Section(webDriver);
        drug2Section = new Drug2Section(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        // do-nothing
    }

    public void postInitialize() {
        regimenElement = WebDriverFactory.createWebElement(regimenElement);
        drugCompositionGroupElement = WebDriverFactory.createWebElement(drugCompositionGroupElement);
        drug1Section.postInitialize();
        drug2Section.postInitialize();
    }

    protected void waitForPageToLoad(Page page) {
        page.waitForDojoElementToLoad(REGIMEN_ID, "dijitInputInner");
    }

    private void setDrugCompositionGroup(String drugCompositionName) {
        drugCompositionGroupElement.sendKeys(drugCompositionName);
    }

    private void setRegimen(String regimenName) {
        regimenElement.sendKeys(regimenName);
    }

    protected void submit() {
        drug1Section.submit();
    }

    protected void fillRegimenSection(TestTreatmentAdvice treatmentAdvice, Page page) {
        setRegimen(treatmentAdvice.regimenName());
        setDrugCompositionGroup(treatmentAdvice.drugCompositionName());
        TestDrugDosage testDrugDosage1 = treatmentAdvice.drugDosages().get(0);
        TestDrugDosage testDrugDosage2 = treatmentAdvice.drugDosages().get(1);
        drug1Section.createDosage(testDrugDosage1, page);
        drug2Section.createDosage(testDrugDosage2, page);
    }
}

