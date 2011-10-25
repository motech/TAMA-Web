package org.motechproject.tama.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.PatientMedicalConditions;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:**/applicationContext.xml")
public class SymptomReportingServiceTest extends SpringIntegrationTest {
    @Autowired
private SymptomReportingService symptomReportingService;

    @Test
    public void shouldReturnRegimen1_1Tree() {
        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();
        patientMedicalConditions.setRegimenName("Regimen I");
        patientMedicalConditions.setAge(20);
        patientMedicalConditions.setCd4Count(10);

        assertEquals("Regimen1_1", symptomReportingService.getSymptomReportingTree(patientMedicalConditions));
    }

    @Test
    public void shouldReturnRegimen1_2Tree() {
        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();
        patientMedicalConditions.setRegimenName("Regimen I");
        patientMedicalConditions.setAge(20);
        patientMedicalConditions.setCd4Count(60);

        assertEquals("Regimen1_2", symptomReportingService.getSymptomReportingTree(patientMedicalConditions));
    }

    @Test
    public void shouldReturnRegimen1_3Tree() {
        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();
        patientMedicalConditions.setRegimenName("Regimen I");
        patientMedicalConditions.setAge(60);
        patientMedicalConditions.setCd4Count(20);
        patientMedicalConditions.setDiabetic(true);
        patientMedicalConditions.setArtRegimenStartDate(DateUtil.today().minusMonths(6));

        assertEquals("Regimen1_3", symptomReportingService.getSymptomReportingTree(patientMedicalConditions));
    }

    @Test
    public void shouldReturnRegimen1_4Tree() {
        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();
        patientMedicalConditions.setRegimenName("Regimen I");
        patientMedicalConditions.setAge(60);
        patientMedicalConditions.setCd4Count(60);
        patientMedicalConditions.setHyperTensic(true);
        patientMedicalConditions.setArtRegimenStartDate(DateUtil.today().minusMonths(6));

        assertEquals("Regimen1_4", symptomReportingService.getSymptomReportingTree(patientMedicalConditions));
    }
}
