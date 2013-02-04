package org.motechproject.tama.web.resportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.tama.web.resportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.resportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AllAppointmentCalendarsBuilder extends InMemoryReportBuilder<ClinicVisit> {

    private PatientReports patientReport;

    public AllAppointmentCalendarsBuilder(List<ClinicVisit> clinicVisits) {
        super(clinicVisits);
    }

    public AllAppointmentCalendarsBuilder(List<ClinicVisit> clinicVisits, PatientReports patientReports) {
        super(clinicVisits);
        this.patientReport = patientReports;
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
        columns = new ArrayList<>();
        columns.add(new ExcelColumn("Patient Id", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Clinic", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Visit Name", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("ART stared on (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Current regimen", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Start date of current regimen (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Appointment Due Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Adjusted Due Date (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Appointment Set for (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Actual Date of Visit (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Type of Visit", Cell.CELL_TYPE_STRING));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        ClinicVisitUIModel clinicVisit = new ClinicVisitUIModel((ClinicVisit) object);
        List<Object> row = new ArrayList<>();
        row.add(patientReport.getPatientReport(clinicVisit.getPatientDocId()).getPatientId());
        row.add(patientReport.getPatientReport(clinicVisit.getPatientDocId()).getClinicName());
        row.add(clinicVisit.getTitle());
        addARTStartDate(patientReport.getPatientReport(clinicVisit.getPatientDocId()), row);
        row.add(patientReport.getPatientReport(clinicVisit.getPatientDocId()).getCurrentRegimenName());
        addCurrentRegimenStartDate(patientReport.getPatientReport(clinicVisit.getPatientDocId()), row);
        addAppointmentDueDate(clinicVisit, row);
        addAdjustedDueDate(clinicVisit, row);
        addAppointmentConfirmedDate(clinicVisit, row);
        addVisitDate(clinicVisit, row);
        row.add(clinicVisit.getTypeOfVisit());
        return row;
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, "Date", DateUtil.today().toString("MMM dd, yyyy"));
    }

    private void addCurrentRegimenStartDate(PatientReport patientReport, List<Object> row) {
        String currentRegimenStartDate = patientReport.getCurrentRegimenStartDate() != null ? DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString("MMM dd, yyyy") : null;
        row.add(currentRegimenStartDate);
    }

    private void addARTStartDate(PatientReport patientReport, List<Object> row) {
        String artStartDate = patientReport.getARTStartedOn() != null ? DateUtil.newDate(patientReport.getARTStartedOn()).toString("MMM dd, yyyy") : null;
        row.add(artStartDate);
    }

    private void addAppointmentDueDate(ClinicVisitUIModel clinicVisit, List<Object> row) {
        String appointmentDueDate = clinicVisit.getAppointmentDueDate() != null ? clinicVisit.getAppointmentDueDate().toLocalDate().toString() : null;
        row.add(appointmentDueDate);
    }

    private void addAdjustedDueDate(ClinicVisitUIModel clinicVisit, List<Object> row) {
        String adjustedDueDate = clinicVisit.getAdjustedDueDate() != null ? clinicVisit.getAdjustedDueDate().toString() : null;
        row.add(adjustedDueDate);
    }

    private void addAppointmentConfirmedDate(ClinicVisitUIModel clinicVisit, List<Object> row) {
        String confirmedAppointmentDate = clinicVisit.getConfirmedAppointmentDate() != null ? clinicVisit.getConfirmedAppointmentDate().toString(TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT) : null;
        row.add(confirmedAppointmentDate);
    }

    private void addVisitDate(ClinicVisitUIModel clinicVisit, List<Object> row) {
        String visitDate = clinicVisit.getVisitDate() != null ? clinicVisit.getVisitDate().toLocalDate().toString() : null;
        visitDate = clinicVisit.isMissed() ? "Missed" : visitDate;
        row.add(visitDate);
    }
}
