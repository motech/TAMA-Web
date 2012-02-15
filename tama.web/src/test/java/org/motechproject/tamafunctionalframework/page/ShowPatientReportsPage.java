package org.motechproject.tamafunctionalframework.page;

import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowPatientReportsPage extends Page {

    private final String PAGE_LOAD_MARKER = "_patientSummaryPatientId_id";

    @FindBy(how = How.ID, using = "_patientSummaryPatientId_patientId_id")
    private WebElement patientId;

    @FindBy(how = How.ID, using = "_patientSummaryClinicName_clinicName_id")
    private WebElement clinicName;

    @FindBy(how = How.ID, using = "dailyPillReminderReportStartDate")
    private WebElement dailyPillReminderReportStartDate;

    @FindBy(how = How.ID, using = "dailyPillReminderReportEndDate")
    private WebElement dailyPillReminderReportEndDate;

    @FindBy(how = How.ID, using = "getDailyPillReminderReport")
    private WebElement generateReportButton;

    @FindBy(how = How.ID, using = "dailyPillReminderReportGrid")
    private WebElement dailyPillReminderReportGrid;

    public ShowPatientReportsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PAGE_LOAD_MARKER);
    }

    public String getClinicName() {
        return clinicName.getText();
    }

    public String getPatientId() {
        return patientId.getText();
    }

    public void generateDailyPillReminderReport(LocalDate startDate, LocalDate endDate){
        this.dailyPillReminderReportStartDate.sendKeys(startDate.toString(TAMAConstants.DATE_FORMAT));
        this.dailyPillReminderReportEndDate.sendKeys(endDate.toString(TAMAConstants.DATE_FORMAT));
        this.generateReportButton.click();
        waitForElementWithIdToLoad(dailyPillReminderReportGrid.getAttribute("id"));
    }

    public String getDailyPillReminderReportText(){
        return this.dailyPillReminderReportGrid.getText();
    }
}
