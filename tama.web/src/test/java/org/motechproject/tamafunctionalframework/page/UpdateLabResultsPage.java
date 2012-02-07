package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

public class UpdateLabResultsPage extends Page {

    public static final String TEST_DATE_ELEMENT = "_labResults[0].testDateAsDate_id";

    @FindBy(how = How.ID, using = "_labResults[0].testDateAsDate_id")
    private WebElement testDateForCD4;

    @FindBy(how = How.ID, using = "_labResults[1].testDateAsDate_id")
    private WebElement testDateForPVL;

    @FindBy(how = How.ID, using = "_labResults[0].result_id")
    private WebElement resultForCD4;

    @FindBy(how = How.ID, using = "_labResults[1].result_id")
    private WebElement resultForPVL;

    public UpdateLabResultsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        testDateForCD4 = WebDriverFactory.createWebElement(testDateForCD4);
        testDateForPVL = WebDriverFactory.createWebElement(testDateForPVL);
        resultForCD4 = WebDriverFactory.createWebElement(resultForCD4);
        resultForPVL = WebDriverFactory.createWebElement(resultForPVL);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(TEST_DATE_ELEMENT);
    }

    public ShowClinicVisitPage update(TestLabResult labResult) {
        waitForDojoElementToLoad(TEST_DATE_ELEMENT, "dijitInputInner");
        enterTestData(labResult);
        results(labResult.results());
        return submit();
    }

    private void enterTestData(TestLabResult labResult) {
        testDateForCD4.sendKeys(labResult.testDates().get(0));
        testDateForPVL.sendKeys(labResult.testDates().get(1));
    }

    private void results(List<String> results) {
        resultForCD4.sendKeys(results.get(0));
        resultForPVL.sendKeys(results.get(1));
    }

    private ShowClinicVisitPage submit() {
        resultForCD4.submit();
        waitForElementWithIdToLoad(ShowTreatmentAdviceSection.PAGE_LOAD_MARKER);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }
}