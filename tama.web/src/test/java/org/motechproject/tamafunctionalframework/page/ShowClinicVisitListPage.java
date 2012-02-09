package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ShowClinicVisitListPage extends Page {

    private static final String PAGE_LOAD_MARKER = "clinicVisitList";

    @FindBy(how = How.ID, using = "visit-0")
    private WebElement firstVisitLink;

    public ShowClinicVisitListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        super.postInitialize();
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PAGE_LOAD_MARKER);
    }

    public CreateClinicVisitPage gotoFirstClinicVisitPage() {
        firstVisitLink.click();
        waitForElementWithIdToLoad(CreateTreatmentAdviceSection.DRUG_BRAND1_ID);
        return MyPageFactory.initElements(webDriver, CreateClinicVisitPage.class);
    }


}
