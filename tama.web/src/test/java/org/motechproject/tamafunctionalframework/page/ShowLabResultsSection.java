package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.Arrays;
import java.util.List;

public class ShowLabResultsSection {

    private static final String EDIT_LAB_RESULT_LINK = "labresults_edit_link";

    @FindBy(how = How.ID, using = "_labResults[0].testDateAsDate_id")
    private WebElement testDateForCD4;

    @FindBy(how = How.ID, using = "_labResults[1].testDateAsDate_id")
    private WebElement testDateForPVL;

    @FindBy(how = How.ID, using = "_labResults[0].result_id")
    private WebElement resultForCD4;

    @FindBy(how = How.ID, using = "_labResults[1].result_id")
    private WebElement resultForPVL;

    @FindBy(how = How.ID, using = "proceed")
    private WebElement submitButton;

    @FindBy(how = How.ID, using = EDIT_LAB_RESULT_LINK)
    private WebElement editLink;

    @FindBy(how = How.XPATH, xpath = "//a[@title='Home']")
    private WebElement homePageLink;


    public List<String> getTestDates() {
        return Arrays.asList(testDateForCD4.getText(), testDateForPVL.getText());
    }

    public List<String> getResults() {
        return Arrays.asList(resultForCD4.getText(), resultForPVL.getText());
    }

    public TestLabResult getLabResult() {
        TestLabResult labResult = new TestLabResult();
        labResult.results(getResults());
        labResult.testDates(getTestDates());
        return labResult;
    }

    public void clickEditLink() {
        editLink.click();
    }
}
