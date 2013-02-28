package org.motechproject.tama.web.reportbuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumnGroup;

import java.util.ArrayList;
import java.util.List;

public class ClinicVisitReportBuilder extends InMemoryReportBuilder<ClinicVisitSummary> {

    public ClinicVisitReportBuilder(List<ClinicVisitSummary> objects) {
        super(objects);
    }

    @Override
    protected String getWorksheetName() {
        return "ClinicVisitReport";
    }

    @Override
    protected String getTitle() {
        return "Clinic Visit Report";
    }

    @Override
    protected void initializeColumns() {
        columns.clear();
        columns.add(new ExcelColumn("Patient ID", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Clinic Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Visit Date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Regimen", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Drug Composition Group", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("Drug Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Dosage", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start evening dose after (days)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Advice", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Meal Advice", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("Drug Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Dosage", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start evening dose after (days)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Advice", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Meal Advice", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("CD4 Test Date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("CD4 Count", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("PVL Test Date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("PVL Count", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("Weight (in kg)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Height (in cm)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Systolic Blood Pressure", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Diastolic Blood Pressure", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Temperature (in F)", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Pulse", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("Opportunistic Infections", Cell.CELL_TYPE_STRING));

        columnGroups.add(new ExcelColumnGroup("Basic Information", Cell.CELL_TYPE_STRING, 0, 0, 4));
        columnGroups.add(new ExcelColumnGroup("Drug 1", Cell.CELL_TYPE_STRING,0, 5, 12));
        columnGroups.add(new ExcelColumnGroup("Drug 2", Cell.CELL_TYPE_STRING,0, 13, 20));
        columnGroups.add(new ExcelColumnGroup("Lab Results", Cell.CELL_TYPE_STRING,0, 21, 24));
        columnGroups.add(new ExcelColumnGroup("Vital Statistics", Cell.CELL_TYPE_STRING,0, 25, 30));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        ClinicVisitSummary summary = (ClinicVisitSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(summary.getPatientReport().getPatientId());
        row.add(summary.getPatientReport().getClinicName());
        row.add(summary.getClinicVisit().getVisitDate());
        row.add(summary.getRegimen().getDisplayName());

        TreatmentAdvice treatmentAdvice = summary.getTreatmentAdvice();
        DrugCompositionGroup drugCompositionGroup = summary.getRegimen().getDrugCompositionGroupFor(treatmentAdvice.getDrugCompositionGroupId());

        row.add(drugCompositionGroup.getName());

        DrugDosage dosage1 = getDrugDosage(treatmentAdvice, 0);

        populateDosage(row, dosage1);

        DrugDosage dosage2 = getDrugDosage(treatmentAdvice, 1);

        populateDosage(row, dosage2);

        LabResults labResults = summary.getLabResults();
        row.add(labResults.latestLabTestDateOf(TAMAConstants.LabTestType.CD4));
        row.add(labResults.latestCountOf(TAMAConstants.LabTestType.CD4));
        row.add(labResults.latestLabTestDateOf(TAMAConstants.LabTestType.PVL));
        row.add(labResults.latestCountOf(TAMAConstants.LabTestType.PVL));

        VitalStatistics vitalStatistics = summary.getVitalStatistics();
        row.add(vitalStatistics.getWeightInKg());
        row.add(vitalStatistics.getHeightInCm());
        row.add(vitalStatistics.getSystolicBp());
        row.add(vitalStatistics.getDiastolicBp());
        row.add(vitalStatistics.getTemperatureInFahrenheit());
        row.add(vitalStatistics.getPulse());

        ReportedOpportunisticInfections reportedOpportunisticInfections = summary.getReportedOpportunisticInfections();
        row.add(StringUtils.join(reportedOpportunisticInfections.getOpportunisticInfectionIds(), ","));

        return row;
    }

    private void populateDosage(List<Object> row, DrugDosage dosage) {
        String drugName = StringUtils.EMPTY;
        String dosageTypeId = StringUtils.EMPTY;
        String morningTime = StringUtils.EMPTY;
        String eveningTime = StringUtils.EMPTY;
        Integer offsetDays = null;
        LocalDate startDate = null;
        String advice = StringUtils.EMPTY;
        String mealAdviceId = StringUtils.EMPTY;

        if(dosage != null) {
            drugName = dosage.getDrugName();
            dosageTypeId = dosage.getDosageTypeId();
            morningTime = dosage.getMorningTime();
            eveningTime = dosage.getEveningTime();
            offsetDays = dosage.getOffsetDays();
            startDate = dosage.getStartDate();
            advice = dosage.getAdvice();
            mealAdviceId = dosage.getMealAdviceId();
        }
        row.add(startDate);
        row.add(drugName);
        row.add(dosageTypeId);
        row.add(morningTime);
        row.add(eveningTime);
        row.add(offsetDays);
        row.add(advice);
        row.add(mealAdviceId);
    }

    private DrugDosage getDrugDosage(TreatmentAdvice advice, int index){
        List<DrugDosage> drugDosages = advice.getDrugDosages();
        if(index >= drugDosages.size())
            return null;

        return drugDosages.get(index);
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {

    }

}
