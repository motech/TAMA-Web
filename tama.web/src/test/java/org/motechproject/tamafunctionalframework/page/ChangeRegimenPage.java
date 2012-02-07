package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ChangeRegimenPage extends Page {

    public static final String DISCONTINUATION_REASON_ID = "_discontinuationReason_id";

    private CreateTreatmentAdviceSection createTreatmentAdviceSection;
    private CreateVitalStatisticsSection createVitalStatisticsSection;
    private CreateLabResultsSection createLabResultsSection;

    @FindBy(how = How.ID, using = DISCONTINUATION_REASON_ID)
    private WebElement discontinuationReasonElement;

    @FindBy(how = How.ID, using = "nextToRegisterNewTreatmentAdvice")
    private WebElement nextToRegisterNewTreatmentAdvice;


    public ChangeRegimenPage(WebDriver webDriver) {
        super(webDriver);
        createTreatmentAdviceSection = PageFactory.initElements(webDriver, CreateTreatmentAdviceSection.class);
        createVitalStatisticsSection = PageFactory.initElements(webDriver, CreateVitalStatisticsSection.class);
        createLabResultsSection = PageFactory.initElements(webDriver, CreateLabResultsSection.class);
    }

    @Override
    public void postInitialize() {
        discontinuationReasonElement = WebDriverFactory.createWebElement(discontinuationReasonElement);
        nextToRegisterNewTreatmentAdvice = WebDriverFactory.createWebElement(nextToRegisterNewTreatmentAdvice);
        createTreatmentAdviceSection.postInitialize();
        createVitalStatisticsSection.postInitialize();
        createLabResultsSection.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad(CreateTreatmentAdviceSection.REGIMEN_ID, "dijitInputInner");
    }

    public ShowClinicVisitPage changeRegimen(TestTreatmentAdvice treatmentAdvice) {
        discontinuationReasonElement.sendKeys(treatmentAdvice.discontinuationReason());
        nextToRegisterNewTreatmentAdvice.click();
        createTreatmentAdviceSection.fillRegimenSection(treatmentAdvice, this);
        createTreatmentAdviceSection.submit();
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }
}