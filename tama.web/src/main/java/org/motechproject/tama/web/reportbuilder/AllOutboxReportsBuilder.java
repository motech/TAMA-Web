package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AllOutboxReportsBuilder extends InMemoryReportBuilder<OutboxMessageSummary> {

    private PatientReports patientReport;

    public AllOutboxReportsBuilder(List<OutboxMessageSummary> objects, PatientReports patientReport) {
        super(objects);
        this.patientReport = patientReport;
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
        columns = new ArrayList<>();
        columns.add(new ExcelColumn("Patient Id", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("ART Started On", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Current Regimen", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start Date of Current Regimen", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Date of Posting (yyyy-mm-dd)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Type of Message", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Date/Time of Playing (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Message Content", Cell.CELL_TYPE_STRING, 10000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        OutboxMessageSummary messageSummary = (OutboxMessageSummary) object;
        List<Object> row = new ArrayList<>();
        row.add(getPatientReport(messageSummary).getPatientId());
        row.add(getPatientReport(messageSummary).getClinicName());
        row.add(getPatientReport(messageSummary).getARTStartedOn());
        row.add(getPatientReport(messageSummary).getCurrentRegimenName());
        row.add(getPatientReport(messageSummary).getCurrentRegimenStartDate());
        row.add(messageSummary.getCreatedOn());
        row.add(messageSummary.getTypeName());
        row.add(messageSummary.getPlayedOn());
        row.add(messageSummary.getPlayedFiles());
        return row;
    }

    private PatientReport getPatientReport(OutboxMessageSummary messageSummary) {
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
