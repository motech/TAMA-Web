package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.common.TAMAConstants.DATE_FORMAT;

public class AppointmentCalendarBuilder extends InMemoryReportBuilder<ClinicVisit> {

    private PatientReport patientReport;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimens allRegimens;
    private static final String CURRENT_REGIMEN = "(Current Regimen)";

    public AppointmentCalendarBuilder(List<ClinicVisit> clinicVisits) {
        super(clinicVisits);
    }

    public AppointmentCalendarBuilder(List<ClinicVisit> clinicVisits, PatientReport patientReport, AllTreatmentAdvices allTreatmentAdvices,
                                      AllRegimens allRegimens) {
        super(clinicVisits);
        this.patientReport = patientReport;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;
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
        columns.add(new ExcelColumn("Appointment Set for (yyyy-mm-dd hh:mm:ss)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Actual Date of Visit (yyyy-mm-dd)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Type of Visit", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Current regimen", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Start date of current regimen (dd-mm-yyyy)", Cell.CELL_TYPE_STRING, 5000));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        ClinicVisitUIModel clinicVisit = new ClinicVisitUIModel((ClinicVisit) object);
        List<Object> row = new ArrayList<Object>();
        ClinicVisit visit = (ClinicVisit) object;
        String appointmentDuedate = clinicVisit.getAppointmentDueDate() != null ? clinicVisit.getAppointmentDueDate().toLocalDate().toString() : null;
        String adjustedDueDate = clinicVisit.getAdjustedDueDate() != null ? clinicVisit.getAdjustedDueDate().toString() : null;
        String confirmedAppointmentDate = clinicVisit.getConfirmedAppointmentDate() != null ? clinicVisit.getConfirmedAppointmentDate().toString(TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT) : null;
        String visitDate = clinicVisit.getVisitDate() != null ? clinicVisit.getVisitDate().toLocalDate().toString() : null;
        visitDate = clinicVisit.isMissed() ? "Missed" : visitDate;
        row.add(clinicVisit.getTitle());
        row.add(appointmentDuedate);
        row.add(adjustedDueDate);
        row.add(confirmedAppointmentDate);
        row.add(visitDate);
        row.add(clinicVisit.getTypeOfVisit());
        if (visit.getTreatmentAdviceId() != null) {
            TreatmentAdvice treatmentAdvice = allTreatmentAdvices.get(visit.getTreatmentAdviceId());
            Regimen regimen = allRegimens.get(treatmentAdvice.getRegimenId());
            String currentRegimenStartDate = treatmentAdvice.getStartDate() != null ? DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT) : null;
            row.add(regimen.getDisplayName());
            row.add(currentRegimenStartDate);
        } else {
            row.add(patientReport.getCurrentRegimenName());
            addCurrentRegimenStartDate(patientReport, row);

        }
        return row;
    }

    private void addCurrentRegimenStartDate(PatientReport patientReport, List<Object> row) {
        String currentRegimenStartDate = patientReport.getCurrentRegimenStartDate() != null ? DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString(TAMAConstants.DATE_FORMAT) : null;
        row.add(currentRegimenStartDate);
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, " ", " ");
        String artStartDate = patientReport.getARTStartedOn() != null ? DateUtil.newDate(patientReport.getARTStartedOn()).toString(TAMAConstants.DATE_FORMAT) : null;
        String currentRegimenStartDate = patientReport.getCurrentRegimenStartDate() != null ? DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString("MMM dd, yyyy") : null;

        buildSummaryRow(worksheet, cellStyles, "Patient Id", patientReport.getPatientId());
        buildSummaryRow(worksheet, cellStyles, "Clinic Name", patientReport.getClinicName());
        buildSummaryRow(worksheet, cellStyles, "ART Started On", artStartDate);
        buildSummaryRow(worksheet, cellStyles, "Current Regimen", patientReport.getCurrentRegimenName());
        buildSummaryRow(worksheet, cellStyles, "Regimen Name ", " Start date ");
        List<TreatmentAdvice> treatmentAdvices = allTreatmentAdvices.find_by_patient_id(patientReport.getPatientDocId());

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
