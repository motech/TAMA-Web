package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ClinicVisitReportBuilder extends InMemoryReportBuilder<DailyPillReminderSummary> {

    private PatientReport patientReport;
    private LocalDate startDate;
    private LocalDate endDate;

    public ClinicVisitReportBuilder(List<DailyPillReminderSummary> objects) {
        super(objects);
    }

    public ClinicVisitReportBuilder(List<DailyPillReminderSummary> objects, PatientReport patientReport, LocalDate startDate, LocalDate endDate) {
        super(objects);
        this.patientReport = patientReport;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected String getWorksheetName() {
        return "DailyPillReminderReport";
    }

    @Override
    protected String getTitle() {
        return "Daily Pill Reminder Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Morning Dose Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Adherence", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Dose Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Adherence", Cell.CELL_TYPE_STRING));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        DailyPillReminderSummary summary = (DailyPillReminderSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(summary.getDate());
        row.add(summary.getMorningDoseTime());
        row.add(summary.getMorningDoseStatus());
        row.add(summary.getEveningDoseTime());
        row.add(summary.getEveningDoseStatus());
        return row;
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, "Patient Id", patientReport.getPatientId());
        buildSummaryRow(worksheet, cellStyles, "Clinic Name", patientReport.getClinicName());
        buildSummaryRow(worksheet, cellStyles, "ART Started On", DateUtil.newDate(patientReport.getARTStartedOn()).toString("MMM dd, yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Current Regimen", patientReport.getCurrentRegimenName());
        buildSummaryRow(worksheet, cellStyles, "Start Date of Current Regimen", DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString("MMM dd, yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Report Start Date", startDate.toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Report End Date", endDate.toString(TAMAConstants.DATE_FORMAT));
    }

}
