package org.motechproject.tama.mapper;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.LabResultBuilder;
import org.motechproject.tama.builder.LabTestBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.domain.*;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PatientMedicalConditionsMapperTest {

    private Patient patient;
    private Regimen regimen;
    private LabResult labResult;

    @Before
    public void setUp(){
        String patientId = "patientId";
        LocalDate dateOfBirth = new LocalDate(1971, 05, 03);
        patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(dateOfBirth).build();

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withName("CD4").build();

        labResult = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult.setLabTest(labTest);

        String regimenName = "Regimen I";
        String regimenId = "regimenId";
        regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();
    }
    
    @Test
    public void mapPatientDetails() {
        PatientMedicalConditionsMapper patientMedicalConditionsMapper = new PatientMedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), regimen);
        PatientMedicalConditions patientMedicalConditions = patientMedicalConditionsMapper.map();

        assertEquals("Male", patientMedicalConditions.getGender());
        assertEquals(40, patientMedicalConditions.getAge());
        assertEquals(60, patientMedicalConditions.getCd4Count());
        assertEquals("Regimen I", patientMedicalConditions.getRegimenName());
    }

    @Test
    public void mapPatientNonHIVMedicalHistory() {
        Ailments ailments = patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories().get(0).getAilments();
        ailments.getAilment(AilmentDefinition.Diabetes).setState(AilmentState.YES);
        ailments.getAilment(AilmentDefinition.Hypertension).setState(AilmentState.NONE);
        ailments.getAilment(AilmentDefinition.Nephrotoxicity).setState(AilmentState.YES_WITH_HISTORY);

        PatientMedicalConditionsMapper patientMedicalConditionsMapper = new PatientMedicalConditionsMapper(patient, new LabResults(Arrays.asList(labResult)), regimen);
        PatientMedicalConditions patientMedicalConditions = patientMedicalConditionsMapper.map();

        assertTrue(patientMedicalConditions.isDiabetic());
        assertFalse(patientMedicalConditions.isHyperTensic());
        assertTrue(patientMedicalConditions.isNephrotoxic());
    }
}
