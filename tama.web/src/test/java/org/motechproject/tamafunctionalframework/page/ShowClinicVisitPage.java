package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ShowClinicVisitPage extends Page {

    private ShowTreatmentAdviceSection treatmentAdviceSection;
    private ShowVitalStatisticsSection vitalStatisticsSection;
    private ShowLabResultsSection labResultsSection;

    @FindBy(how = How.ID, using = "showPatient")
    private WebElement showPatientLink;

    public ShowClinicVisitPage(WebDriver webDriver) {
        super(webDriver);
        treatmentAdviceSection = PageFactory.initElements(webDriver, ShowTreatmentAdviceSection.class);
        vitalStatisticsSection = PageFactory.initElements(webDriver, ShowVitalStatisticsSection.class);
        labResultsSection = PageFactory.initElements(webDriver, ShowLabResultsSection.class);
    }

    @Override
    public void postInitialize() {
        super.postInitialize();
        this.showPatientLink = WebDriverFactory.createWebElement(this.showPatientLink);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
    }

    public TestTreatmentAdvice getTreatmentAdvice() {
        return treatmentAdviceSection.getTreatmentAdvice();
    }

    public TestVitalStatistics getVitalStatistics() {
        return vitalStatisticsSection.getVitalStatistics();
    }

    public TestLabResult getLabResult() {
        return labResultsSection.getLabResult();
    }

    public ChangeRegimenPage clickChangeRegimenLink() {
        treatmentAdviceSection.clickChangeRegimen();
        waitForElementWithIdToLoad(ChangeRegimenPage.DISCONTINUATION_REASON_ID);
        return MyPageFactory.initElements(webDriver, ChangeRegimenPage.class);
    }

    public UpdateLabResultsPage clickEditLabResultLink() {
        labResultsSection.clickEditLink();
        waitForElementWithIdToLoad(UpdateLabResultsPage.TEST_DATE_ELEMENT);
        return MyPageFactory.initElements(webDriver, UpdateLabResultsPage.class);
    }

    public UpdateVitalStatisticsPage clickEditVitalStatisticsLink() {
        vitalStatisticsSection.clickEdit();
        waitForElementWithIdToLoad(UpdateVitalStatisticsPage.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, UpdateVitalStatisticsPage.class);
    }

    public ShowPatientPage gotoShowPatientPage() {
        showPatientLink.click();
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }
}
