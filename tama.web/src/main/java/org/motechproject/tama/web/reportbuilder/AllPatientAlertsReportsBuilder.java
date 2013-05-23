package org.motechproject.tama.web.reportbuilder;


import org.apache.poi.ss.usermodel.Cell;

import org.motechproject.tama.patient.domain.PatientAlert;

import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;

import java.util.ArrayList;

import java.util.List;

public class AllPatientAlertsReportsBuilder extends InMemoryReportBuilder<PatientAlert> {

    private PatientReports patientReports;

    public AllPatientAlertsReportsBuilder(PatientReports patientReports, List<PatientAlert> alerts, String patientId) {
        super(alerts);
        this.patientReports = patientReports;

    }


    @Override
    protected String getWorksheetName() {
        return "PatientAlertsReport";
    }

    @Override
    protected String getTitle() {
        return "Patient Alerts Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<>();
        columns.add(new ExcelColumn("Patient Id", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Alert Type", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Alert Generated on", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Alert Generated Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Alert Status", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Alert Priority", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Description", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Appointment Due Date", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Appointment Confirmed Date)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Call preference", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Symptoms Reported", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("TAMA Advice", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Symptom Call Status", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Connected To Doctor", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Doctors Notes Based On Direct Contact", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Notes", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Change In Patient Status Due To TAMA Advice ", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Current Patient Status", Cell.CELL_TYPE_STRING));
    }


    @Override
    protected List<Object> getRowData(Object object) {

        List<Object> row = new ArrayList<>();
        PatientReport patientReportSummary = patientReports.getPatientReport(patientReports.getPatientDocIds().get(0));
        PatientAlert patientAlert = (PatientAlert) object;
        populateColumnValuesForEachAlert(patientReportSummary, patientAlert, row);
        return row;
    }

    private List<Object> populateColumnValuesForEachAlert(PatientReport patientReport, PatientAlert patientAlert, List<Object> row) {
        if (null != patientAlert) {

            String patientAlertType = patientAlert.getType().getDisplayName();
            row.add(patientReport.getPatientId());
            row.add(patientReport.getClinicName());
            row.add(patientAlertType);
            row.add(patientAlert.getGeneratedOnDate());
            row.add(patientAlert.getGeneratedOnTime());
            row.add(patientAlert.getAlertStatus());


            if (PatientAlertType.SymptomReporting.getDisplayName().equals(patientAlertType)) {
                populateSymptomAlertValues(patientReport, patientAlert, row);
            } else if (PatientAlertType.FallingAdherence.getDisplayName().equals(patientAlertType) || PatientAlertType.AdherenceInRed.getDisplayName().equals(patientAlertType)) {
                populateFallingAdherenceAlertValues(patientReport, patientAlert, row);
            } else if (PatientAlertType.AppointmentReminder.getDisplayName().equals(patientAlertType) || PatientAlertType.AppointmentConfirmationMissed.getDisplayName().equals(patientAlertType)) {
                populateAppointmentReminderAlertValues(patientReport, patientAlert, row);
            } else if (PatientAlertType.VisitMissed.getDisplayName().equals(patientAlertType)) {
                populateVisitMissedAlertValues(patientReport, patientAlert, row);
            }
        }
        return row;
    }


    private List<Object> populateVisitMissedAlertValues(PatientReport patientReport, PatientAlert patientAlert, List<Object> row) {
        row.add(null);
        row.add(null);
        row.add(patientAlert.getAppointmentDueDate().toString());
        row.add(patientAlert.getConfirmedAppointmentDateTime().toString());
        row.add(patientAlert.getPatientCallPreference());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getNotes());
        row.add(patientAlert.getStatusAction());
        row.add(patientAlert.getPatient().getStatus());
        return row;
    }


    private List<Object> populateSymptomAlertValues(PatientReport patientReport, PatientAlert patientAlert, List<Object> row) {
        row.add(patientAlert.getAlertPriority());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getSymptomReported());
        row.add(patientAlert.getAdviceGiven());
        row.add(patientAlert.getSymptomAlertStatus());
        row.add(patientAlert.getConnectedToDoctor());
        row.add(patientAlert.getDoctorsNotes());
        row.add(patientAlert.getNotes());
        row.add(patientReport.getPatient().getStatus());
        row.add(patientAlert.getPatient().getStatus());
        return row;
    }

    private List<Object> populateFallingAdherenceAlertValues(PatientReport patientReport, PatientAlert patientAlert, List<Object> row) {
        row.add(null);
        row.add(patientAlert.getDescription());
        row.add(null);
        row.add(null);
        row.add(patientAlert.getPatientCallPreference());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getNotes());
        row.add(patientAlert.getStatusAction());
        row.add(patientAlert.getPatient().getStatus());
        return row;
    }

    private List<Object> populateAppointmentReminderAlertValues(PatientReport patientReport, PatientAlert patientAlert, List<Object> row) {
        row.add(null);
        row.add(null);
        row.add(patientAlert.getAppointmentDueDate().toString());
        row.add(null);
        row.add(patientAlert.getPatientCallPreference());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getNotes());
        row.add(patientAlert.getStatusAction());
        row.add(patientAlert.getPatient().getStatus());
        return row;
    }

    private PatientReport getPatientReport(String patientId) {
        PatientReport patientReport = patientReports.getPatientReport(patientId);
        if (null == patientReport) {
            return PatientReport.nullPatientReport();
        }
        return patientReport;

    }
}
