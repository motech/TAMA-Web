package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.domain.OutboxSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class OutboxReportBuilder extends ReportBuilder<OutboxSummary> {

    private PatientReport patientSummary;
    private LocalDate startDate;
    private LocalDate endDate;

    public OutboxReportBuilder(List<OutboxSummary> objects) {
        super(objects);
    }

    public OutboxReportBuilder(List<OutboxSummary> objects, PatientReport patientSummary, LocalDate startDate, LocalDate endDate) {
        super(objects);
        this.patientSummary = patientSummary;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected String getWorksheetName() {
        return "OutboxReport";
    }

    @Override
    protected String getTitle() {
        return "Outbox Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Date of Posting (yyyy-mm-dd)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Type of Message", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Date/Time of Playing (yyyy-mm-dd hh:mm)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Message Content", Cell.CELL_TYPE_STRING, 10000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        OutboxSummary summary = (OutboxSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(summary.getCreatedOn());
        row.add(summary.getTypeName());
        row.add(summary.getPlayedOn());
        row.add(summary.getPlayedFiles());
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
