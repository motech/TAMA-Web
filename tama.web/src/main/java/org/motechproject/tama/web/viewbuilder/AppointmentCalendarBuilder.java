package org.motechproject.tama.web.viewbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AppointmentCalendarBuilder extends ReportBuilder<ClinicVisitUIModel> {

    private PatientReport patientReport;

    public AppointmentCalendarBuilder(List<ClinicVisitUIModel> clinicVisits) {
        super(clinicVisits);
    }

    public AppointmentCalendarBuilder(List<ClinicVisitUIModel> clinicVisits, PatientReport patientReport) {
        super(clinicVisits);
        this.patientReport = patientReport;
    }

    @Override
    protected String getWorksheetName() {
        return "AppointmentCalendar";
    }

    @Override
    protected String getTitle() {
        return "Appointment Calendar";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Visit Name", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Appointment Due Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Adjusted Due Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Appointment Set for (yyyy-mm-dd hh:mm)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Actual Date of Visit (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Type of Visit", Cell.CELL_TYPE_STRING));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        ClinicVisitUIModel clinicVisit = (ClinicVisitUIModel) object;
        List<Object> row = new ArrayList<Object>();
        String appointmentDuedate = clinicVisit.getAppointmentDueDate() != null ? clinicVisit.getAppointmentDueDate().toLocalDate().toString() : null;
        String adjustedDueDate = clinicVisit.getAdjustedDueDate() != null ? clinicVisit.getAdjustedDueDate().toString() : null;
        String confirmedAppointmentDate = clinicVisit.getConfirmedAppointmentDate() != null ? clinicVisit.getConfirmedAppointmentDate().toString("yyyy-MM-dd HH:mm") : null;
        String visitDate = clinicVisit.getVisitDate() != null ? clinicVisit.getVisitDate().toLocalDate().toString() : null;
        visitDate = clinicVisit.isMissed() ? "Missed" : visitDate;

        row.add(clinicVisit.getTitle());
        row.add(appointmentDuedate);
        row.add(adjustedDueDate);
        row.add(confirmedAppointmentDate);
        row.add(visitDate);
        row.add(clinicVisit.getTypeOfVisit());
        return row;
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        String artStartDate = patientReport.getARTStartedOn() != null ? DateUtil.newDate(patientReport.getARTStartedOn()).toString("MMM dd, yyyy") : null;
        String currentRegimenStartDate = patientReport.getCurrentRegimenStartDate() != null ? DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString("MMM dd, yyyy") : null;

        buildSummaryRow(worksheet, cellStyles, "Patient Id", patientReport.getPatientId());
        buildSummaryRow(worksheet, cellStyles, "Clinic Name", patientReport.getClinicName());
        buildSummaryRow(worksheet, cellStyles, "ART Started On", artStartDate);
        buildSummaryRow(worksheet, cellStyles, "Current Regimen", patientReport.getCurrentRegimenName());
        buildSummaryRow(worksheet, cellStyles, "Start Date of Current Regimen", currentRegimenStartDate);
    }
}
