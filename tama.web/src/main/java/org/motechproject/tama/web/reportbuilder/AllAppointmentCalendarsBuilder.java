package org.motechproject.tama.web.reportbuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AllAppointmentCalendarsBuilder extends InMemoryReportBuilder<ClinicVisit> {

    private PatientReports patientReports;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimens allRegimens;
    private static final String CURRENT_REGIMEN = "(Current Regimen)";

    public AllAppointmentCalendarsBuilder(List<ClinicVisit> clinicVisits) {
        super(clinicVisits);
    }

    public AllAppointmentCalendarsBuilder(List<ClinicVisit> clinicVisits, PatientReports patientReports,AllTreatmentAdvices allTreatmentAdvices,AllRegimens allRegimens) {
        super(clinicVisits);
        this.patientReports = patientReports;
        this.allTreatmentAdvices=allTreatmentAdvices;
        this.allRegimens=allRegimens;
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
        ClinicVisit visit =  (ClinicVisit) object;
        row.add(patientReports.getPatientReport(clinicVisit.getPatientDocId()).getPatientId());
        row.add(patientReports.getPatientReport(clinicVisit.getPatientDocId()).getClinicName());
        row.add(clinicVisit.getTitle());
        addARTStartDate(patientReports.getPatientReport(clinicVisit.getPatientDocId()), row);
        if(visit.getTreatmentAdviceId()!=null)
        {
            TreatmentAdvice treatmentAdvice = allTreatmentAdvices.get(visit.getTreatmentAdviceId());
            Regimen regimen = allRegimens.get(treatmentAdvice.getRegimenId());
            String currentRegimenStartDate = treatmentAdvice.getStartDate() != null ? DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT) : null;
            row.add(regimen.getDisplayName());
            row.add(currentRegimenStartDate);
        }
        else
        {
         row.add(patientReports.getPatientReport(clinicVisit.getPatientDocId()).getCurrentRegimenName());
         addCurrentRegimenStartDate(patientReports.getPatientReport(clinicVisit.getPatientDocId()), row);
        }
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

        buildSummaryRow(worksheet, cellStyles, "Date", DateUtil.today().toString("dd/MM/yyyy"));

        buildSummaryRow(worksheet, cellStyles, " ", " ");
        List<String> patientDocumentIds =  patientReports.getPatientDocIds();

        for(String patientDocumentId: patientDocumentIds )
        {

            PatientReport report = patientReports.getPatientReport(patientDocumentId);
            buildSummaryRow(worksheet, cellStyles, "Patient Id", report.getPatientId());
            buildSummaryRow(worksheet, cellStyles, "Clinic Name", report.getClinicName());
            buildSummaryRow(worksheet, cellStyles, "ART Started On", DateUtil.newDate(report.getARTStartedOn()).toString(TAMAConstants.DATE_FORMAT));
            buildSummaryRow(worksheet, cellStyles, "Regimen Change History", "  ");
            buildSummaryRow(worksheet, cellStyles, "Regimen Name ", " Start date ");
            List<TreatmentAdvice> treatmentAdvices = allTreatmentAdvices.find_by_patient_id(report.getPatientDocId());
            Collections.sort(treatmentAdvices);
            for(TreatmentAdvice treatmentAdvice :treatmentAdvices)
            {
                if(treatmentAdvice.getEndDate()==null)
                    buildSummaryRow(worksheet, cellStyles,  allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName() + " " +CURRENT_REGIMEN,
                            DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT));
                else
                    buildSummaryRow(worksheet, cellStyles,  allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName(),
                            DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT));

            }
            buildSummaryRow(worksheet, cellStyles, "            ", "      ");
            worksheet.createRow(worksheet.getLastRowNum()+1);
            buildSummaryRow(worksheet, cellStyles, "             ", " ");

        }
    }

    private void addCurrentRegimenStartDate(PatientReport patientReport, List<Object> row) {
        String currentRegimenStartDate = patientReport.getCurrentRegimenStartDate() != null ? DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString(TAMAConstants.DATE_FORMAT) : null;
        row.add(currentRegimenStartDate);
    }

    private void addARTStartDate(PatientReport patientReport, List<Object> row) {
        String artStartDate = patientReport.getARTStartedOn() != null ? DateUtil.newDate(patientReport.getARTStartedOn()).toString(TAMAConstants.DATE_FORMAT) : null;
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
        String visitDate = clinicVisit.getVisitDate() != null ? DateUtil.newDate(clinicVisit.getVisitDate()).toString(TAMAConstants.DATE_FORMAT) : null;
        visitDate = clinicVisit.isMissed() ? "Missed" : visitDate;
        row.add(visitDate);
    }
}
