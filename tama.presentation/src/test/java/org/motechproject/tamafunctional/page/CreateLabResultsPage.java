package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

public class CreateLabResultsPage extends Page {

    public static final String TEST_DATE_ELEMENT = "_labResults[0].testDateAsDate_id";

    @FindBy(how = How.ID, using = "_labResults[0].testDateAsDate_id")
    private WebElement testDateForCD4;

    @FindBy(how = How.ID, using = "_labResults[1].testDateAsDate_id")
    private WebElement testDateForPVL;

    @FindBy(how = How.ID, using = "_labResults[0].result_id")
    private WebElement resultForCD4;

    @FindBy(how = How.ID, using = "_labResults[1].result_id")
    private WebElement resultForPVL;

    public CreateLabResultsPage(WebDriver webDriver) {
        super(webDriver);
    }

    public ShowLabResultsPage registerNewLabResult(TestLabResult labResult) {
        waitForDojoElementToLoad(TEST_DATE_ELEMENT, "dijitInputInner");
        enterTestData(labResult);
        results(labResult.results());
        return submit();
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

    private void enterTestData(TestLabResult labResult) {
        testDateForCD4.sendKeys(labResult.testDates().get(0));
        testDateForPVL.sendKeys(labResult.testDates().get(1));
    }

    public void results(List<String> results) {
        resultForCD4.sendKeys(results.get(0));
        resultForPVL.sendKeys(results.get(1));
    }

    public ShowLabResultsPage submit() {
        resultForCD4.submit();
        waitForElementWithIdToLoad(ShowLabResultsPage.EDIT_LAB_RESULT_LINK);
        return MyPageFactory.initElements(webDriver, ShowLabResultsPage.class);
    }
}
