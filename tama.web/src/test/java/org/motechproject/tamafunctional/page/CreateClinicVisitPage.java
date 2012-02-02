package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class CreateClinicVisitPage extends Page {

    private CreateTreatmentAdviceSection createTreatmentAdviceSection;
    private CreateVitalStatisticsSection createVitalStatisticsSection;
    private CreateLabResultsSection createLabResultsSection;

    public CreateClinicVisitPage(WebDriver webDriver) {
        super(webDriver);
        createTreatmentAdviceSection = PageFactory.initElements(webDriver, CreateTreatmentAdviceSection.class);
        createVitalStatisticsSection = PageFactory.initElements(webDriver, CreateVitalStatisticsSection.class);
        createLabResultsSection = PageFactory.initElements(webDriver, CreateLabResultsSection.class);
    }

    @Override
    public void postInitialize() {
        createTreatmentAdviceSection.postInitialize();
        createVitalStatisticsSection.postInitialize();
        createLabResultsSection.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad(CreateTreatmentAdviceSection.REGIMEN_ID, "dijitInputInner");
    }

    public ShowClinicVisitPage createNewRegimen(TestTreatmentAdvice treatmentAdvice) {
        createTreatmentAdviceSection.fillRegimenSection(treatmentAdvice, this);
        createTreatmentAdviceSection.submit();
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }

    public ShowClinicVisitPage createNewRegimen(TestTreatmentAdvice treatmentAdvice, TestVitalStatistics vitalStatistics) {
        createTreatmentAdviceSection.fillRegimenSection(treatmentAdvice, this);
        createVitalStatisticsSection.fillVitalStatistics(vitalStatistics);
        createTreatmentAdviceSection.submit();
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }

    public ShowClinicVisitPage createNewRegimen(TestTreatmentAdvice treatmentAdvice, TestLabResult labResult) {
        createTreatmentAdviceSection.fillRegimenSection(treatmentAdvice, this);
        createLabResultsSection.fillLabResults(labResult, this);
        createTreatmentAdviceSection.submit();
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }

    public ShowClinicVisitPage createNewRegimen(TestTreatmentAdvice treatmentAdvice, TestLabResult labResult, TestVitalStatistics vitalStatistics) {
        createTreatmentAdviceSection.fillRegimenSection(treatmentAdvice, this);
        createLabResultsSection.fillLabResults(labResult, this);
        createVitalStatisticsSection.fillVitalStatistics(vitalStatistics);
        createTreatmentAdviceSection.submit();
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }

}
