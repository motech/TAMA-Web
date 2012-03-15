package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DailyPillReminderReportBuilder extends ReportBuilder<DailyPillReminderSummary> {

    private PatientReport patientSummary;
    private LocalDate startDate;
    private LocalDate endDate;

    public DailyPillReminderReportBuilder(List<DailyPillReminderSummary> objects) {
        super(objects);
    }

    public DailyPillReminderReportBuilder(List<DailyPillReminderSummary> objects, PatientReport patientSummary, LocalDate startDate, LocalDate endDate) {
        super(objects);
        this.patientSummary = patientSummary;
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
        buildSummaryRow(worksheet, cellStyles, "Patient Id", patientSummary.getPatientId());
        buildSummaryRow(worksheet, cellStyles, "Clinic Name", patientSummary.getClinicName());
        buildSummaryRow(worksheet, cellStyles, "ART Started On", DateUtil.newDate(patientSummary.getARTStartedOn()).toString("MMM dd, yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Current Regimen", patientSummary.getCurrentRegimenName());
        buildSummaryRow(worksheet, cellStyles, "Start Date of Current Regimen", DateUtil.newDate(patientSummary.getCurrentRegimenStartDate()).toString("MMM dd, yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Report Start Date", startDate.toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Report End Date", endDate.toString(TAMAConstants.DATE_FORMAT));
    }

}
