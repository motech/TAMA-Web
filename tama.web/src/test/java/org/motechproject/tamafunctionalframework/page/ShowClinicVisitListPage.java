package org.motechproject.tamafunctionalframework.page;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.util.DateUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowClinicVisitListPage extends Page {

    private static final String PAGE_LOAD_MARKER = "clinicVisitList";

    @FindBy(how = How.ID, using = "visit-0")
    private WebElement firstVisitLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='adjustDueDateAsToday']")
    WebElement adjustDueDateAsTodayLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='adjustDueDate']")
    WebElement adjustDueDateLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//td[@class='appointmentDueDateColumn']")
    WebElement dueDate;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='markAsMissed']")
    WebElement markAsMissedLink;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//td[@class='visitDateColumn']")
    WebElement visitDateColumn;

    @FindBy(how = How.XPATH, using="//tr[@visitid='week4']//a[@class='confirmVisitDate']")
    WebElement scheduleConfirmVisitDateLink;

    @FindBy(how = How.ID, using="confirmok")
    WebElement confirmok;

    @FindBy(how = How.ID, using="save")
    WebElement popupSaveButton;

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

    public DateTime getDueDate() throws ParseException {
        Date parsedDate = new SimpleDateFormat(TAMAConstants.DATE_FORMAT).parse(dueDate.getText());
        return DateUtil.newDateTime(new LocalDate(parsedDate), 0, 0, 0);
    }

    public void scheduleConfirmVisitDateAsToday() {
        scheduleConfirmVisitDateLink.click();
        popupSaveButton.click();
        String today = DateUtil.today().toString(TAMAConstants.DATE_FORMAT);
        waitForElementWithXPATHToLoad("//tr[@visitid='week4']//td[@class='confirmVisitDateColumn']/a[contains(., '" + today + "')]");
    }

    public DateTime getConfirmVisitDate() throws ParseException {
        Date parsedDate = new SimpleDateFormat(TAMAConstants.DATETIME_FORMAT).parse(scheduleConfirmVisitDateLink.getText());
        return DateUtil.newDateTime(new LocalDate(parsedDate), 0, 0, 0);
    }
}
