package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class ShowARTRegimenPage extends Page {

    public static final String REGIMEN_TEXT_ID = "_c_org_motechproject_tama_domain_TreatmentAdvice_regimens_regimenName_id";

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_TreatmentAdvice_patientId_patientId_id")
    private WebElement patientIdElement;

    @FindBy(how = How.ID, using = REGIMEN_TEXT_ID)
    private WebElement regimenNameElement;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_TreatmentAdvice_regimencompositions_drugCompositionName_id")
    private WebElement drugCompositionGroupNameElement;

    @FindBy(how = How.ID, using = "changeRegimen")
    private WebElement changeRegimenElement;

    public ShowARTRegimenPage(WebDriver webDriver) {
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

    public String getDrugCompositionGroupName() {
        return drugCompositionGroupNameElement.getText();
    }

    public CreateARTRegimenPage goToChangeARTRegimenPage() {
        this.changeRegimenElement.click();
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.findElement(By.id(CreateARTRegimenPage.DISCONTINUATION_REASON_ID)) != null;
            }
        });
        return MyPageFactory.initElements(webDriver, CreateARTRegimenPage.class);
    }
}
