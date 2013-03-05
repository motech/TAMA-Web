package org.motechproject.tama.web.reportbuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.contract.DrugDosageContract;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumn;
import org.motechproject.tama.web.reportbuilder.model.ExcelColumnGroup;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.common.util.DateFormat.format;

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
        columns.add(new ExcelColumn("Start evening dose after (days)", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Start date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Advice", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Meal Advice", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("Drug Name", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Dosage", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Morning Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Evening Time", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Start evening dose after (days)", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Start date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Advice", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("Meal Advice", Cell.CELL_TYPE_STRING));

        columns.add(new ExcelColumn("CD4 Test Date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("CD4 Count", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("PVL Test Date", Cell.CELL_TYPE_STRING));
        columns.add(new ExcelColumn("PVL Count", Cell.CELL_TYPE_NUMERIC));

        columns.add(new ExcelColumn("Weight (in kg)", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Height (in cm)", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Systolic Blood Pressure", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Diastolic Blood Pressure", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Temperature (in F)", Cell.CELL_TYPE_NUMERIC));
        columns.add(new ExcelColumn("Pulse", Cell.CELL_TYPE_NUMERIC));

        columns.add(new ExcelColumn("Opportunistic Infections", Cell.CELL_TYPE_STRING));

        columnGroups.add(new ExcelColumnGroup("Basic Information", Cell.CELL_TYPE_STRING, 0, 0, 4));
        columnGroups.add(new ExcelColumnGroup("Drug 1", Cell.CELL_TYPE_STRING, 0, 5, 12));
        columnGroups.add(new ExcelColumnGroup("Drug 2", Cell.CELL_TYPE_STRING, 0, 13, 20));
        columnGroups.add(new ExcelColumnGroup("Lab Results", Cell.CELL_TYPE_STRING, 0, 21, 24));
        columnGroups.add(new ExcelColumnGroup("Vital Statistics", Cell.CELL_TYPE_STRING, 0, 25, 30));
    }

    @Override
    protected List<Object> getRowData(Object object) {
        ClinicVisitSummary summary = (ClinicVisitSummary) object;
        List<Object> row = new ArrayList<Object>();
        row.add(summary.getPatientReport().getPatientId());
        row.add(summary.getPatientReport().getClinicName());

        row.add(format(summary.getVisitDate().toDate(), "dd/MM/yyyy"));

        row.add(summary.getRegimen().getDisplayName());
        row.add(summary.getDrugCompositonGroupName());

        populateDosage(row, summary.getDrugDosageOne());
        populateDosage(row, summary.getDrugDosageTwo());

        populateLabResults(row, summary.getLabResults());

        populateVitalStatistics(row, summary.getVitalStatistics());

        row.add(summary.getReportedOpportunisticInfections());

        return row;
    }

    private void populateVitalStatistics(List<Object> row, VitalStatistics vitalStatistics) {
        Double weightInKg = null;
        Double heightInCm = null;
        Integer systolicBp = null;
        Integer diastolicBp = null;
        Double temperatureInFahrenheit = null;
        Integer pulse = null;

        if (vitalStatistics != null) {
            weightInKg = vitalStatistics.getWeightInKg();
            heightInCm = vitalStatistics.getHeightInCm();
            systolicBp = vitalStatistics.getSystolicBp();
            diastolicBp = vitalStatistics.getDiastolicBp();
            temperatureInFahrenheit = vitalStatistics.getTemperatureInFahrenheit();
            pulse = vitalStatistics.getPulse();
        }

        row.add(weightInKg);
        row.add(heightInCm);
        row.add(systolicBp);
        row.add(diastolicBp);
        row.add(temperatureInFahrenheit);
        row.add(pulse);
    }

    private void populateLabResults(List<Object> row, LabResults labResults) {
        LocalDate cd4TestDate = null;
        Integer cd4Count = null;
        LocalDate pvlTestDate = null;
        Integer pvlCount = null;

        if (labResults != null) {
            cd4TestDate = labResults.latestLabTestDateOf(TAMAConstants.LabTestType.CD4);
            cd4Count = labResults.latestCountOf(TAMAConstants.LabTestType.CD4);
            pvlTestDate = labResults.latestLabTestDateOf(TAMAConstants.LabTestType.PVL);
            pvlCount = labResults.latestCountOf(TAMAConstants.LabTestType.PVL);

            cd4Count = cd4Count == -1 ? null : cd4Count;
            pvlCount = pvlCount == -1 ? null : pvlCount;
        }

        row.add(cd4TestDate);
        row.add(cd4Count);
        row.add(pvlTestDate);
        row.add(pvlCount);
    }


    private void populateDosage(List<Object> row, DrugDosageContract dosage) {
        String drugName = StringUtils.EMPTY;
        String dosageTypeId = StringUtils.EMPTY;
        String morningTime = StringUtils.EMPTY;
        String eveningTime = StringUtils.EMPTY;
        Integer offsetDays = null;
        String startDate = null;
        String advice = StringUtils.EMPTY;
        String mealAdviceId = StringUtils.EMPTY;

        if (dosage != null) {
            drugName = dosage.getDrugName();
            dosageTypeId = dosage.getDosageType();
            morningTime = dosage.getMorningTime();
            eveningTime = dosage.getEveningTime();
            offsetDays = dosage.getOffsetDays();
            startDate = format(dosage.getStartDate(), "dd/mm/yyyy");
            advice = dosage.getAdvice();
            mealAdviceId = dosage.getMealAdvice();
        }
        row.add(drugName);
        row.add(dosageTypeId);
        row.add(morningTime);
        row.add(eveningTime);
        row.add(offsetDays);
        row.add(startDate);
        row.add(advice);
        row.add(mealAdviceId);
    }

    @Override
    protected void buildSummary(HSSFSheet worksheet) {

    }

}
