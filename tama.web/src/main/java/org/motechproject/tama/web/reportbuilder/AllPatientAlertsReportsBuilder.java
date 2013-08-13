package org.motechproject.tama.web.reportbuilder;


import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.motechproject.tama.patient.domain.PatientAlert;

import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import java.util.List;

public class AllPatientAlertsReportsBuilder extends InMemoryReportBuilder<PatientAlert> {

    private PatientReports patientReports;
    private DateTime alertStartDate;
    private DateTime alertEndDate;

    public AllPatientAlertsReportsBuilder(PatientReports patientReports, List<PatientAlert> alerts, String patientId,DateTime alertStartDate,DateTime alertEndDate) {
        super(alerts);
        this.patientReports = patientReports;
        this.alertStartDate = alertStartDate;
        this.alertEndDate = alertEndDate;
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
        columns.add(new ExcelColumn("Current Patient Status", Cell.CELL_TYPE_STRING));
    }


    @Override
    protected List<Object> getRowData(Object object) {

        List<Object> row = new ArrayList<>();
        PatientReport patientReportSummary = null;
        if(patientReports.getPatientDocIds().size()!=0)
        {
                patientReports.getPatientReport(patientReports.getPatientDocIds().get(0));
        }
        PatientAlert patientAlert = (PatientAlert) object;
        populateColumnValuesForEachAlert(patientReportSummary, patientAlert, row);
        return row;
    }

    private List<Object> populateColumnValuesForEachAlert(PatientReport patientReport, PatientAlert patientAlert, List<Object> row) {
        if (null != patientAlert) {

            String patientAlertType = patientAlert.getType().getDisplayName();
            row.add(patientAlert.getPatientId());
            row.add(patientAlert.getPatient().getClinic().getName());
            row.add(patientAlertType);
            row.add(patientAlert.getGeneratedOnDate());
            row.add(patientAlert.getGeneratedOnTime());
            row.add(patientAlert.getAlertStatus());


            if (PatientAlertType.SymptomReporting.getDisplayName().equals(patientAlertType)) {
                populateSymptomAlertValues(patientAlert, row);
            } else if (PatientAlertType.FallingAdherence.getDisplayName().equals(patientAlertType) || PatientAlertType.AdherenceInRed.getDisplayName().equals(patientAlertType)) {
                populateFallingAdherenceAlertValues(patientAlert, row);
            } else if (PatientAlertType.AppointmentReminder.getDisplayName().equals(patientAlertType) || PatientAlertType.AppointmentConfirmationMissed.getDisplayName().equals(patientAlertType)) {
                populateAppointmentReminderAlertValues(patientAlert, row);
            } else if (PatientAlertType.VisitMissed.getDisplayName().equals(patientAlertType)) {
                populateVisitMissedAlertValues(patientAlert, row);
            }
            row.add(patientAlert.getPatient().getStatus());
        }
        return row;
    }


    private List<Object> populateVisitMissedAlertValues(PatientAlert patientAlert, List<Object> row) {
        String date=getFormattedDateTime("dd/MM/yyyy h:mm aa",patientAlert.getConfirmedAppointmentDateTime().toDateTime());
        DateTime datetime =  DateTime.parse(patientAlert.getAppointmentDueDate().toString());
        String appointmentDueDate = getFormattedDateTime("dd/MM/yyyy",datetime);
        row.add(null);
        row.add(null);
        row.add(appointmentDueDate);
        row.add(date);
        row.add(patientAlert.getPatientCallPreference());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getNotes());

        return row;
    }

    private String getFormattedDateTime(String format,DateTime dateTime){
        return DateTimeFormat.forPattern(format).print(dateTime);
    }

    private List<Object> populateSymptomAlertValues(PatientAlert patientAlert, List<Object> row) {
        String yes="Yes";
        String no="No";
        String connectedToDoctor = patientAlert.getConnectedToDoctor();
        if("NA".equals(connectedToDoctor))
        {
            connectedToDoctor=no;
        }
        if(!no.equalsIgnoreCase(connectedToDoctor))
        {
            connectedToDoctor=yes;
        }
        row.add(patientAlert.getAlertPriority());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getSymptomReported());
        row.add(patientAlert.getAdviceGiven());
        row.add(patientAlert.getSymptomAlertStatus());
        row.add(connectedToDoctor);
        row.add(patientAlert.getDoctorsNotes());
        row.add(patientAlert.getNotes());

        return row;
    }

    private List<Object> populateFallingAdherenceAlertValues(PatientAlert patientAlert, List<Object> row) {
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

        return row;
    }

    private List<Object> populateAppointmentReminderAlertValues(PatientAlert patientAlert, List<Object> row) {
        DateTime date =  DateTime.parse(patientAlert.getAppointmentDueDate().toString());
        String appointmentDueDate = getFormattedDateTime("dd/MM/yyyy",date);
        row.add(null);
        row.add(null);
        row.add(appointmentDueDate);
        row.add(null);
        row.add(patientAlert.getPatientCallPreference());
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(null);
        row.add(patientAlert.getNotes());

        return row;
    }

    private PatientReport getPatientReport(String patientId) {
        PatientReport patientReport = patientReports.getPatientReport(patientId);
        if (null == patientReport) {
            return PatientReport.nullPatientReport();
        }
        return patientReport;

    }
    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);

        buildSummaryRow(worksheet, cellStyles, "Date", DateUtil.today().toString("dd/MM/yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Report Start Date", alertStartDate.toString("dd/MM/yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Report End Date", alertEndDate.toString("dd/MM/yyyy"));
        buildSummaryRow(worksheet, cellStyles, " ", " ");

    }
}
