package org.motechproject.tama.web.resportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.web.resportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.resportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class OutboxReportBuilder extends InMemoryReportBuilder<OutboxMessageSummary> {

    private PatientReport patientSummary;
    private LocalDate startDate;
    private LocalDate endDate;

    public OutboxReportBuilder(List<OutboxMessageSummary> objects) {
        super(objects);
    }

    public OutboxReportBuilder(List<OutboxMessageSummary> objects, PatientReport patientSummary, LocalDate startDate, LocalDate endDate) {
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
        columns.add(new ExcelColumn("Date/Time of Playing (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Message Content", Cell.CELL_TYPE_STRING, 10000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        OutboxMessageSummary messageSummary = (OutboxMessageSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(messageSummary.getCreatedOn());
        row.add(messageSummary.getTypeName());
        row.add(messageSummary.getPlayedOn());
        row.add(messageSummary.getPlayedFiles());
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
