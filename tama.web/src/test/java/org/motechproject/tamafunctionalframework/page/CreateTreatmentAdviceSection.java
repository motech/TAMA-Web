package org.motechproject.tamafunctionalframework.page;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

public class CreateTreatmentAdviceSection extends Page {

    public static final String REGIMEN_ID = "_treatmentAdvice.regimenId_id";

    public static final String DRUG_BRAND1_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_drugName1_drugName_id";

    @FindBy(how = How.ID, using = REGIMEN_ID)
    private WebElement regimenElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionGroupId_id")
    private WebElement drugCompositionGroupElement;

    @FindBy(how = How.ID, using = "_treatmentAdvice.drugCompositionId_id")
    private WebElement drugNameElement;

    private DrugDosageSection[] drugDosageSections = new DrugDosageSection[2];

    public CreateTreatmentAdviceSection(WebDriver webDriver) {
        super(webDriver);
        drugDosageSections[0] = new Drug1Section(webDriver);
        drugDosageSections[1] = new Drug2Section(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        // do-nothing
    }

    public void postInitialize() {
        regimenElement = WebDriverFactory.createWebElement(regimenElement);
        drugNameElement = WebDriverFactory.createWebElement(drugNameElement);
        drugCompositionGroupElement = WebDriverFactory.createWebElement(drugCompositionGroupElement);
        for (DrugDosageSection drugDosageSection : drugDosageSections) {
            drugDosageSection.postInitialize();
        }
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

    private void setDrugName(TestTreatmentAdvice testTreatmentAdvice) {
        if (StringUtils.isNotEmpty(testTreatmentAdvice.drugName())) {
            drugNameElement.sendKeys(testTreatmentAdvice.drugName());
        }
    }

    protected void submit() {
        drugDosageSections[0].submit();
    }

    protected void fillRegimenSection(TestTreatmentAdvice treatmentAdvice, Page page) {
        setRegimen(treatmentAdvice.regimenName());
        setDrugCompositionGroup(treatmentAdvice.drugCompositionName());
        setDrugName(treatmentAdvice);

        List<TestDrugDosage> testDrugDosages = treatmentAdvice.drugDosages();
        for (int i = 0; i < testDrugDosages.size(); i++) {
            drugDosageSections[i].createDosage(testDrugDosages.get(i), page);
        }
    }
}

