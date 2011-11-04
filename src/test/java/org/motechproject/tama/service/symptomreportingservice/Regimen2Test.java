package org.motechproject.tama.service.symptomreportingservice;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.service.SymptomReportingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:**/applicationContext.xml")
public class Regimen2Test extends SpringIntegrationTest {
    @Autowired
    private SymptomReportingService symptomReportingService;
    private MedicalCondition medicalCondition;

    @Before
    public void setUp() {
        medicalCondition = new MedicalCondition();
    }

    private LocalDate today() {
        return DateUtil.today();
    }

    private String execute() {
        return symptomReportingService.getSymptomReportingTree(medicalCondition);
    }

    @Test
    public void shouldReturnRegimen2_1Tree() {
        medicalCondition.regimenName("Regimen II");
        assertEquals("Regimen2_1", execute());
    }

    @Test
    public void shouldReturnRegimen2_2Tree_Diabetic_Only() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(false).diabetic(true).age(60).treatmentStartDate(today().minusMonths(6));
        assertEquals("Regimen2_2", execute());
    }

    @Test
    public void shouldReturnRegimen2_2Tree_HyperTensic_Only() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(false).hyperTensic(true).age(60).treatmentStartDate(today().minusMonths(6));
        assertEquals("Regimen2_2", execute());
    }

    @Test
    public void shouldReturnRegimen2_2Tree_NephroToxic_Only() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(false).nephrotoxic(true).age(60).treatmentStartDate(today().minusMonths(6));
        assertEquals("Regimen2_2", execute());
    }

    @Test
    public void shouldReturnRegimen2_3() {
        medicalCondition.regimenName("Regimen II").cd4Count(90).psychiatricIllness(false).nephrotoxic(true).age(60).treatmentStartDate(today().minusMonths(6));
        assertEquals("Regimen2_3", execute());
    }

    @Test
    public void shouldReturnRegimen2_4() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(true);
        assertEquals("Regimen2_4", execute());
    }

    @Test
    public void shouldReturnRegimen2_5() {
        medicalCondition.regimenName("Regimen II").cd4Count(90).psychiatricIllness(true);
        assertEquals("Regimen2_5_7", execute());
    }

    @Test
    public void shouldReturnRegimen2_6() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(true).diabetic(true).age(60).treatmentStartDate(today().minusMonths(6));
        assertEquals("Regimen2_6", execute());
    }

    @Test
    public void shouldReturnRegimen2_7() {
        medicalCondition.regimenName("Regimen II").cd4Count(90).psychiatricIllness(true).diabetic(true).age(60).treatmentStartDate(today().minusMonths(6));
        assertEquals("Regimen2_5_7", execute());
    }
}
