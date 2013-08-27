package org.motechproject.tama.web.reportbuilder;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AllDailyPillReminderReportsBuilder extends InMemoryReportBuilder<DailyPillReminderSummary> {

    private PatientReports patientReports;
    private AllRegimens allRegimens;
    private AllTreatmentAdvices allTreatmentAdvices;
    private LocalDate startDate;
    private LocalDate endDate;
    private static final String CURRENT_REGIMEN = "(Current Regimen)";
    private static final String ON_DAILY_PILL_REMINDER = "Daily";
    private boolean shouldAddSummary = true;

    public AllDailyPillReminderReportsBuilder(List<DailyPillReminderSummary> objects, PatientReports patientReports, AllRegimens allRegimens, AllTreatmentAdvices allTreatmentAdvices,
                                              LocalDate startDate, LocalDate endDate, boolean shouldAddSummary) {
        super(objects);
        this.patientReports = patientReports;
        this.allRegimens = allRegimens;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.startDate = startDate;
        this.endDate = endDate;
        this.shouldAddSummary = shouldAddSummary;
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
        columns = new ArrayList<>();
        columns.add(new ExcelColumn("Patient Id", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("ART Started On (dd-mm-yyyy)", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Current Regimen", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Start Date of Current Regimen", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Medicine adherence report calls", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Date of daily Adherence  (dd-mm-yyyy)", Cell.CELL_TYPE_STRING, 5000));
        columns.add(new ExcelColumn("Morning Pill Time hh:mm", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Morning Adherence", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Evening Pill Time hh:mm", Cell.CELL_TYPE_STRING, 10000));
        columns.add(new ExcelColumn("Evening Adherence", Cell.CELL_TYPE_STRING, 10000));

    }

    @Override
    protected List<Object> getRowData(Object object) {
        DailyPillReminderSummary messageSummary = (DailyPillReminderSummary) object;
        List<Object> row = new ArrayList<>();
        PatientReport patientReportSummary = getPatientReport(messageSummary);
        String artStartDate = patientReportSummary.getARTStartedOn() != null ? DateUtil.newDate(patientReportSummary.getARTStartedOn()).toString(TAMAConstants.DATE_FORMAT) : null;
        String currentRegimenStartDate = getRegimenStartDate(messageSummary.getTreatmentAdviceId());
        row.add(patientReportSummary.getPatientId());
        row.add(patientReportSummary.getClinicName());
        row.add(artStartDate);
        row.add(getRegimenName((messageSummary.getTreatmentAdviceId())));
        row.add(currentRegimenStartDate);
        row.add(ON_DAILY_PILL_REMINDER);
        row.add(messageSummary.getDate());
        row.add(messageSummary.getMorningDoseTime());
        row.add(messageSummary.getMorningDoseStatus());
        row.add(messageSummary.getEveningDoseTime());
        row.add(messageSummary.getEveningDoseStatus());
        return row;
    }

    private PatientReport getPatientReport(DailyPillReminderSummary messageSummary) {
        PatientReport report = patientReports.getPatientReport(messageSummary.getPatientDocId());
        if (null == report) {
            return PatientReport.nullPatientReport();
        }
        return report;
    }

    private String getRegimenName(String treatmentAdviceId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        return allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName();
    }

    private String getRegimenStartDate(String treatmentAdviceId) {
        return DateUtil.newDate(allTreatmentAdvices.get(treatmentAdviceId).getStartDate()).toString("dd/MM/yyyy");
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {
        List<HSSFCellStyle> cellStyles = buildCellStylesForSummary(worksheet);
        buildSummaryRow(worksheet, cellStyles, "Date", DateUtil.today().toString("dd/MM/yyyy"));

        buildSummaryRow(worksheet, cellStyles, "Reports Start Date", startDate.toString("dd/MM/yyyy"));
        buildSummaryRow(worksheet, cellStyles, "Reports End Date", endDate.toString("dd/MM/yyyy"));
        buildSummaryRow(worksheet, cellStyles, "                ", " ");
        if (shouldAddSummary) {
            List<String> patientDocumentIds = patientReports.getPatientDocIds();

            for (String patientDocumentId : patientDocumentIds) {

                PatientReport report = patientReports.getPatientReport(patientDocumentId);
                if (report.getPatient().isOnDailyPillReminder()) {
                    buildSummaryRow(worksheet, cellStyles, "Patient Id", report.getPatientId());
                    buildSummaryRow(worksheet, cellStyles, "Clinic Name", report.getClinicName());
                    buildSummaryRow(worksheet, cellStyles, "ART Started On", DateUtil.newDate(report.getARTStartedOn()).toString(TAMAConstants.DATE_FORMAT));
                    buildSummaryRow(worksheet, cellStyles, "Regimen Change History", "  ");
                    buildSummaryRow(worksheet, cellStyles, "Regimen Name ", " Start date ");
                    List<TreatmentAdvice> treatmentAdvices = allTreatmentAdvices.find_by_patient_id(report.getPatientDocId());

                    for (TreatmentAdvice treatmentAdvice : treatmentAdvices) {
                        if (treatmentAdvice.getEndDate() == null)
                            buildSummaryRow(worksheet, cellStyles, allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName() + " " + CURRENT_REGIMEN,
                                    DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT));
                        else
                            buildSummaryRow(worksheet, cellStyles, allRegimens.get(treatmentAdvice.getRegimenId()).getDisplayName(),
                                    DateUtil.newDate(treatmentAdvice.getStartDate()).toString(TAMAConstants.DATE_FORMAT));

                    }
                    buildSummaryRow(worksheet, cellStyles, "            ", "      ");
                    worksheet.createRow(worksheet.getLastRowNum() + 1);
                    buildSummaryRow(worksheet, cellStyles, "             ", "      ");
                }
            }
        }

    }
}
