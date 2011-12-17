package org.motechproject.tamacallflow.mapper;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.MedicalHistoryBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

public class MedicalConditionsMapperTest {

    private Patient patient;
    private LabResult labResult;
    private VitalStatistics vitalStatistics;
    private TreatmentAdvice treatmentAdvice;
    private Regimen regimen;

    @Before
    public void setUp() {
        String patientId = "patientId";
        LocalDate dateOfBirth = new LocalDate(1971, 05, 03);
        patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(dateOfBirth).build();

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();

        labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult.setLabTest(labTest);

        String regimenName = "Regimen I";
        String regimenId = "regimenId";
        regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();

        vitalStatistics = new VitalStatistics(74.00, 174.00, 10, 10, 10.00, 10, patientId);
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(DateUtil.today().minusMonths(4)).build();

        NonHIVMedicalHistory nonHivMedicalHistory = new NonHIVMedicalHistory();
        nonHivMedicalHistory.addSystemCategory(systemCategory(SystemCategoryDefinition.Other), systemCategory(SystemCategoryDefinition.Psychiatric));
        nonHivMedicalHistory.setQuestions(MedicalHistoryQuestions.all());
        patient.setMedicalHistory(MedicalHistoryBuilder.startRecording().withDefaults().withNonHIVMedicalHistory(nonHivMedicalHistory).build());
    }

    private SystemCategory systemCategory(SystemCategoryDefinition categoryDefinition) {
        return new SystemCategory(categoryDefinition.getCategoryName(), categoryDefinition.getAilments());
    }

    @Test
    public void mapPatientDetails() {
        MedicalConditionsMapper mapper = new MedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), vitalStatistics, treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        Assert.assertEquals("Male", medicalCondition.gender());
        Assert.assertEquals(40, medicalCondition.age());
        Assert.assertEquals(60, medicalCondition.cd4Count());
        Assert.assertEquals("Regimen I", medicalCondition.regimenName());
    }

    @Test
    public void mapPatientNonHIVMedicalHistory() {
        getNonHIVMedicalHistory(patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories().get(0).getAilments());

        SystemCategory psychiatricIllnessSystemCategory = patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories().get(1);
        psychiatricIllnessSystemCategory.getAilments().getOtherAilments().get(0).setState(AilmentState.YES);

        MedicalHistoryQuestion baselineHBQuestion = patient.getMedicalHistory().getNonHivMedicalHistory().getQuestions().get(1);
        baselineHBQuestion.setHistoryPresent(true);

        MedicalConditionsMapper mapper = new MedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), vitalStatistics, treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        Assert.assertTrue(medicalCondition.isDiabetic());
        Assert.assertFalse(medicalCondition.isHyperTensic());
        Assert.assertTrue(medicalCondition.isNephrotoxic());
        Assert.assertTrue(medicalCondition.lowBaselineHBCount());
        Assert.assertTrue(medicalCondition.psychiatricIllness());
        Assert.assertTrue(medicalCondition.isAlcoholic());
        Assert.assertTrue(medicalCondition.isTuberculosis());
    }

    @Test
    public void mapTreatmentAdviceDuration() {
        MedicalConditionsMapper mapper = new MedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), vitalStatistics, treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        Assert.assertEquals(4, medicalCondition.numberOfMonthsSinceTreatmentStarted());
    }

    @Test
    public void mapBMIOnVitalStatistics() {
        MedicalConditionsMapper mapper = new MedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), vitalStatistics, treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        Assert.assertEquals(24.44, medicalCondition.bmi());
    }

    private void getNonHIVMedicalHistory(Ailments ailments) {
        ailments.getAilment(AilmentDefinition.Diabetes).setState(AilmentState.YES);
        ailments.getAilment(AilmentDefinition.Hypertension).setState(AilmentState.NONE);
        ailments.getAilment(AilmentDefinition.Nephrotoxicity).setState(AilmentState.YES_WITH_HISTORY);
        ailments.getAilment(AilmentDefinition.Alcoholism).setState(AilmentState.YES_WITH_HISTORY);
        ailments.getAilment(AilmentDefinition.Tuberculosis).setState(AilmentState.YES);
    }
}
