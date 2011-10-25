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

public class PatientMedicalConditionsMapperTest {

    public PatientMedicalConditionsMapper patientMedicalConditionsMapper;

    @Before
    public void setUp(){
        String patientId = "patientId";
        LocalDate dateOfBirth = new LocalDate(1971, 05, 03);
        Patient patient = PatientBuilder.startRecording().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(dateOfBirth).build();

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withName("CD4").build();

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult1.setLabTest(labTest);

        String regimenName = "Regimen I";
        String regimenId = "regimenId";
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();

        patientMedicalConditionsMapper = new PatientMedicalConditionsMapper(patient, Arrays.asList(labResult1), regimen);
    }
    
    @Test
    public void mapPatientDetails() {
        PatientMedicalConditions patientMedicalConditions = patientMedicalConditionsMapper.map();

        assertEquals("Male", patientMedicalConditions.getGender());
        assertEquals(40, patientMedicalConditions.getAge());
        assertEquals(60, patientMedicalConditions.getCd4Count());
        assertEquals("Regimen I", patientMedicalConditions.getRegimenName());
    }
}
