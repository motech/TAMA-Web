package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.Arrays;
import java.util.List;

public class ShowLabResultsPage extends Page {

    public static final String TEST_DATE_ELEMENT = "_labResults[0].testDateAsDate_id";

    public static final String EDIT_LAB_RESULT_LINK = "labresults_edit_link";

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


    public ShowLabResultsPage(WebDriver webDriver) {
        super(webDriver);
    }

    public List<String> getTestDates() {
        return Arrays.asList(testDateForCD4.getText(), testDateForPVL.getText());
    }

    public List<String> getResults() {
        return Arrays.asList(resultForCD4.getText(), resultForPVL.getText());
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(EDIT_LAB_RESULT_LINK);
    }

    public ListPatientsPage gotoHomePage() {
        homePageLink.click();
        waitForElementWithIdToLoad(ListPatientsPage.LIST_PATIENT_PANE_ID);
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class);
    }

    public UpdateLabResultsPage gotoEditPage() {
        editLink.click();
        waitForElementWithIdToLoad(TEST_DATE_ELEMENT);
        return MyPageFactory.initElements(webDriver, UpdateLabResultsPage.class);
    }
}
