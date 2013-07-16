package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.common.TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT;


public class DailyPillReminderReportBuilder extends InMemoryReportBuilder<DailyPillReminderSummary> {

    private PatientReport patientReport;
    private LocalDate startDate;
    private LocalDate endDate;
    private AllRegimens allRegimens;
    private AllTreatmentAdvices allTreatmentAdvices;

    public DailyPillReminderReportBuilder(List<DailyPillReminderSummary> objects) {
        super(objects);
    }

    public DailyPillReminderReportBuilder(List<DailyPillReminderSummary> objects, PatientReport patientReport, LocalDate startDate, LocalDate endDate,AllRegimens allRegimens,AllTreatmentAdvices allTreatmentAdvices) {
        super(objects);
        this.patientReport = patientReport;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allRegimens = allRegimens;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    @Override
    protected String getWorksheetName() {
        return "DailyPillReminderReport";
    }

    @Override
    protected String getTitle() {
        return "Daily Pill Reminder Report";
    }

    @Override
    protected void initializeColumns() {
        columns = new ArrayList<ExcelColumn>();
        columns.add(new ExcelColumn("Date of daily Adherence  (dd-mm-yyyy)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Morning Pill Time hh:mm", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Adherence", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Pill Time hh:mm", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Adherence", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Current Regimen", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start Date of Current Regimen", Cell.CELL_TYPE_STRING));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        DailyPillReminderSummary summary = (DailyPillReminderSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(summary.getDate());
        row.add(summary.getMorningDoseTime());
        row.add(summary.getMorningDoseStatus());
        row.add(summary.getEveningDoseTime());
        row.add(summary.getEveningDoseStatus());
        String currentRegimenStartDate = getRegimenStartDate(summary.getTreatmentAdviceId());
        row.add(getRegimenName((summary.getTreatmentAdviceId())));
        row.add(currentRegimenStartDate);
        return row;
    }

    private String getRegimenName(String treatmentAdviceId)
    {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        return allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName();
    }
    private String getRegimenStartDate(String treatmentAdviceId)
    {
        return DateUtil.newDate(allTreatmentAdvices.get(treatmentAdviceId).getStartDate()).toString("dd/MM/yyyy");
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, " ", " ");
        buildSummaryRow(worksheet, cellStyles, "Patient Id", patientReport.getPatientId());
        buildSummaryRow(worksheet, cellStyles, "Clinic Name", patientReport.getClinicName());
        buildSummaryRow(worksheet, cellStyles, "ART Started On", DateUtil.newDate(patientReport.getARTStartedOn()).toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Current Regimen", patientReport.getCurrentRegimenName());
        buildSummaryRow(worksheet, cellStyles, "Start Date of Current Regimen", DateUtil.newDate(patientReport.getCurrentRegimenStartDate()).toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Report Start Date", startDate.toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Report End Date", endDate.toString(TAMAConstants.DATE_FORMAT));
        buildSummaryRow(worksheet, cellStyles, "Regimen Change History", "  ");
        buildSummaryRow(worksheet, cellStyles, "Regimen Name ", " Start date ");
        List<TreatmentAdvice> treatmentAdvices = allTreatmentAdvices.find_by_patient_id(patientReport.getPatientDocId());

        for(TreatmentAdvice treatmentAdvice :treatmentAdvices)
        {
            buildSummaryRow(worksheet, cellStyles,  allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName(),
                    DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT));
        }
        buildSummaryRow(worksheet, cellStyles, " ", " ");
        buildSummaryRow(worksheet, cellStyles, " ", " ");
    }
}
