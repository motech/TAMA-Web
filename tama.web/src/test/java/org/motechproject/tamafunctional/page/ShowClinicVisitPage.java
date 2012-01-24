package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class ShowClinicVisitPage extends Page {

    private ShowTreatmentAdviceSection treatmentAdviceSection;
    private ShowVitalStatisticsSection vitalStatisticsSection;
    private ShowLabResultsSection labResultsSection;


    public ShowClinicVisitPage(WebDriver webDriver) {
        super(webDriver);
        treatmentAdviceSection = PageFactory.initElements(webDriver, ShowTreatmentAdviceSection.class);
        vitalStatisticsSection = PageFactory.initElements(webDriver, ShowVitalStatisticsSection.class);
        labResultsSection = PageFactory.initElements(webDriver, ShowLabResultsSection.class);
    }

    @Override
    public void postInitialize() {
        super.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
    }

//    public String getRegimenName() {
//        return treatmentAdviceSection.getRegimenName();
//    }
//
//    public String getDrugCompositionGroupName() {
//        return treatmentAdviceSection.getDrugCompositionGroupName();
//    }
//
//    public CreateARTRegimenPage goToChangeARTRegimenPage() {
//        treatmentAdviceSection.clickChangeRegimen();
//        waitForElementWithIdToLoad(CreateARTRegimenPage.DISCONTINUATION_REASON_ID);
//        return MyPageFactory.initElements(webDriver, CreateARTRegimenPage.class);
//    }

    public TestVitalStatistics getVitalStatistics() {
        return vitalStatisticsSection.getVitalStatistics();
    }

    public TestLabResult getLabResult() {
        return labResultsSection.getLabResult();
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
}
