package org.motechproject.tamafunctionalframework.page;

import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowPatientReportsPage extends Page {

    public static final String DAILY_PILL_REMINDER_REPORT_GRID = "dailyPillReminderReportGrid";
    private final String PAGE_LOAD_MARKER = "fc_org_motechproject_tama_domain_patient_Patient_Summary";


    @FindBy(how = How.ID, using = "dailyPillReminderReportStartDate")
    private WebElement dailyPillReminderReportStartDate;

    @FindBy(how = How.ID, using = "dailyPillReminderReportEndDate")
    private WebElement dailyPillReminderReportEndDate;

    @FindBy(how = How.ID, using = "getDailyPillReminderReport")
    private WebElement generateReportButton;

    @FindBy(how = How.ID, using = DAILY_PILL_REMINDER_REPORT_GRID)
    private WebElement dailyPillReminderReportGrid;

    public ShowPatientReportsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        dailyPillReminderReportStartDate = WebDriverFactory.createWebElement(dailyPillReminderReportStartDate);
        dailyPillReminderReportEndDate = WebDriverFactory.createWebElement(dailyPillReminderReportEndDate);
        generateReportButton = WebDriverFactory.createWebElement(generateReportButton);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PAGE_LOAD_MARKER);
    }

    public void generateDailyPillReminderReport(LocalDate startDate, LocalDate endDate){
        this.dailyPillReminderReportStartDate.sendKeys(startDate.toString(TAMAConstants.DATE_FORMAT));
        this.dailyPillReminderReportEndDate.sendKeys(endDate.toString(TAMAConstants.DATE_FORMAT));
        this.generateReportButton.click();
        waitForElementWithIdToLoad(DAILY_PILL_REMINDER_REPORT_GRID);
    }

    public String getDailyPillReminderReportText(){
        waitForElementWithCSSToLoad("td.dojoxGridCell"); //wait for grid cell to load
        return this.dailyPillReminderReportGrid.getText();
    }
}
