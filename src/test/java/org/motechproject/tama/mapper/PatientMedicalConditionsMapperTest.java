package org.motechproject.tama.mapper;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.*;
import org.motechproject.tama.domain.*;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.*;

public class PatientMedicalConditionsMapperTest {

    private Patient patient;
    private LabResult labResult;
    private TreatmentAdvice treatmentAdvice;
    private Regimen regimen;

    @Before
    public void setUp(){
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
        PatientMedicalConditionsMapper mapper = new PatientMedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        assertEquals("Male", medicalCondition.gender());
        assertEquals(40, medicalCondition.age());
        assertEquals(60, medicalCondition.cd4Count());
        assertEquals("Regimen I", medicalCondition.regimenName());
    }

    @Test
    public void mapPatientNonHIVMedicalHistory() {
        Ailments ailments = patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories().get(0).getAilments();
        ailments.getAilment(AilmentDefinition.Diabetes).setState(AilmentState.YES);
        ailments.getAilment(AilmentDefinition.Hypertension).setState(AilmentState.NONE);
        ailments.getAilment(AilmentDefinition.Nephrotoxicity).setState(AilmentState.YES_WITH_HISTORY);

        SystemCategory psychiatricIllnessSystemCategory = patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories().get(1);
        psychiatricIllnessSystemCategory.getAilments().getOtherAilments().get(0).setState(AilmentState.YES);

        MedicalHistoryQuestion baselineHBQuestion = patient.getMedicalHistory().getNonHivMedicalHistory().getQuestions().get(1);
        baselineHBQuestion.setHistoryPresent(true);

        PatientMedicalConditionsMapper mapper = new PatientMedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        assertTrue(medicalCondition.isDiabetic());
        assertFalse(medicalCondition.isHyperTensic());
        assertTrue(medicalCondition.isNephrotoxic());
        assertTrue(medicalCondition.lowBaselineHBCount());
        assertTrue(medicalCondition.psychiatricIllness());
    }

    @Test
    public void mapTreatmentAdviceDuration() {
        PatientMedicalConditionsMapper mapper = new PatientMedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), treatmentAdvice, regimen);
        MedicalCondition medicalCondition = mapper.map();

        assertEquals(4, medicalCondition.numberOfMonthsSinceRegimenStarted());
    }


}
