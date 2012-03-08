package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.util.DateUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowClinicVisitListPage extends Page {

    private static final String PAGE_LOAD_MARKER = "clinicVisitList";

    @FindBy(how = How.ID, using = "visit-0")
    private WebElement firstVisitLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='adjustDueDateAsToday']")
    WebElement adjustDueDateAsTodayLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='adjustDueDate']")
    WebElement adjustDueDateLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='markAsMissed']")
    WebElement markAsMissedLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//td[@class='visitDateColumn']")
    WebElement visitDateColumn;

    @FindBy(how = How.ID, using="confirmok")
    WebElement confirmok;

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

    public CreateClinicVisitPage gotoFirstCreateClinicVisitPage() {
        firstVisitLink.click();
        waitForElementWithIdToLoad(CreateTreatmentAdviceSection.DRUG_BRAND1_ID);
        return MyPageFactory.initElements(webDriver, CreateClinicVisitPage.class);
    }

    public ShowClinicVisitPage gotoFirstShowClinicVisitPage() {
        firstVisitLink.click();
        waitForElementWithIdToLoad(CreateTreatmentAdviceSection.DRUG_BRAND1_ID);
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }

    public String getFirstVisitDescription() {
        return firstVisitLink.getText();
    }

    public void adjustDueDateAsToday(){
        adjustDueDateAsTodayLink.click();
        String today = DateUtil.today().toString(TAMAConstants.DATE_FORMAT);
        waitForElementWithXPATHToLoad("//tr[@visitid='week4']//td[@class='adjustDueDateColumn' and a='" + today + "']");
    }

    public String getAdjustedDueDate(){
        return adjustDueDateLink.getText();
    }

    public void markAsMissed(){
        markAsMissedLink.click();
        confirmok.click();
        waitForElementWithXPATHToLoad("//tr[@visitid='week4' and td='Missed']");
    }

    public String getVisitDate(){
        return visitDateColumn.getText();
    }
}
