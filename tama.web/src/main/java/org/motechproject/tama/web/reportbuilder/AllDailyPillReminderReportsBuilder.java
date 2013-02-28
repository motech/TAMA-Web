package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AllDailyPillReminderReportsBuilder extends InMemoryReportBuilder<DailyPillReminderSummary> {

    private PatientReports patientReport;

    public AllDailyPillReminderReportsBuilder(List<DailyPillReminderSummary> objects, PatientReports patientReport) {
        super(objects);
        this.patientReport = patientReport;
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
        columns = new ArrayList<>();
        columns.add(new ExcelColumn("Patient Id", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("ART Started On", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Current Regimen", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start Date of Current Regimen", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Morning Dose Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Adherence", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Dose Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Adherence", Cell.CELL_TYPE_STRING));

    }

    @Override
    protected List<Object> getRowData(Object object) {
        DailyPillReminderSummary messageSummary = (DailyPillReminderSummary) object;
        List<Object> row = new ArrayList<>();
        row.add(getPatientReport(messageSummary).getPatientId());
        row.add(getPatientReport(messageSummary).getClinicName());
        row.add(getPatientReport(messageSummary).getARTStartedOn());
        row.add(getPatientReport(messageSummary).getCurrentRegimenName());
        row.add(getPatientReport(messageSummary).getCurrentRegimenStartDate());
        row.add(messageSummary.getDate());
        row.add(messageSummary.getMorningDoseTime());
        row.add(messageSummary.getMorningDoseStatus());
        row.add(messageSummary.getEveningDoseTime());
        row.add(messageSummary.getEveningDoseStatus());
        return row;
    }

    private PatientReport getPatientReport(DailyPillReminderSummary messageSummary) {
        PatientReport report = patientReport.getPatientReport(messageSummary.getPatientDocId());
        if (null == report) {
            return PatientReport.nullPatientReport();
        }
        return report;
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, "Date", DateUtil.today().toString("yyyy-MM-dd"));
    }
}
