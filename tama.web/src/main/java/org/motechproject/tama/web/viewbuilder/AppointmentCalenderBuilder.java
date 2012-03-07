package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AppointmentCalenderBuilder extends ReportBuilder<ClinicVisitUIModel> {

    private PatientReport patientReport;

    public AppointmentCalenderBuilder(List<ClinicVisitUIModel> clinicVisits) {
        super(clinicVisits);
    }

    public AppointmentCalenderBuilder(List<ClinicVisitUIModel> clinicVisits, PatientReport patientReport) {
        super(clinicVisits);
        this.patientReport = patientReport;
    }

    @Override
    protected String getWorksheetName() {
        return "AppointmentCalender";
    }

    @Override
    protected String getTitle() {
        return "Appointment Calender";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Visit Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Appointment Due Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Adjusted Due Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Scheduled Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Visit Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Visit Type", Cell.CELL_TYPE_STRING));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        ClinicVisitUIModel clinicVisit = (ClinicVisitUIModel) object;
        List<Object> row = new ArrayList<Object>();
        row.add(clinicVisit.getTitle());
        row.add(clinicVisit.getAppointmentDueDate());
        row.add(clinicVisit.getAdjustedDueDate());
        row.add(clinicVisit.getConfirmedAppointmentDate());
        row.add(clinicVisit.getVisitDate());
        row.add(clinicVisit.getTypeOfVisit());
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
    }
}
