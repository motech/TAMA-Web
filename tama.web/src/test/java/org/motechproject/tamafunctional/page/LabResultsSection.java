package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

public class LabResultsSection {

    public static final String TEST_DATE_ELEMENT = "_labResultsUIModel.labResults[0].testDateAsDate_id";

    @FindBy(how = How.ID, using = "_labResultsUIModel.labResults[0].testDateAsDate_id")
    private WebElement testDateForCD4;

    @FindBy(how = How.ID, using = "_labResultsUIModel.labResults[1].testDateAsDate_id")
    private WebElement testDateForPVL;

    @FindBy(how = How.ID, using = "_labResultsUIModel.labResults[0].result_id")
    private WebElement resultForCD4;

    @FindBy(how = How.ID, using = "_labResultsUIModel.labResults[1].result_id")
    private WebElement resultForPVL;

    public void postInitialize() {
        testDateForCD4 = WebDriverFactory.createWebElement(testDateForCD4);
        testDateForPVL = WebDriverFactory.createWebElement(testDateForPVL);
        resultForCD4 = WebDriverFactory.createWebElement(resultForCD4);
        resultForPVL = WebDriverFactory.createWebElement(resultForPVL);
    }

    public void enterLabResults(TestLabResult labResult, Page page) {
        page.waitForDojoElementToLoad(TEST_DATE_ELEMENT, "dijitInputInner");
        enterTestData(labResult);
        results(labResult.results());
    }

    private void enterTestData(TestLabResult labResult) {
        testDateForCD4.sendKeys(labResult.testDates().get(0));
        testDateForPVL.sendKeys(labResult.testDates().get(1));
    }

    private void results(List<String> results) {
        resultForCD4.sendKeys(results.get(0));
        resultForPVL.sendKeys(results.get(1));
    }
}
