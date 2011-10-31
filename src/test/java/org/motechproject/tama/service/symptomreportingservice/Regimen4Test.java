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
public class Regimen4Test extends SpringIntegrationTest {
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
    public void shouldReturnRegimen4_1Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(10).psychiatricIllness(false).lowBaselineHBCount(true);
        assertEquals("Regimen4_1_6", execute());
    }

    @Test
    public void shouldReturnRegimen4_2Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(90).psychiatricIllness(false).lowBaselineHBCount(true);
        assertEquals("Regimen4_2", execute());
    }

    @Test
    public void shouldReturnRegimen4_3Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(10).psychiatricIllness(true).lowBaselineHBCount(false);
        assertEquals("Regimen4_3", execute());
    }

    @Test
    public void shouldReturnRegimen4_4Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(90).psychiatricIllness(true).lowBaselineHBCount(false);
        assertEquals("Regimen4_4", execute());
    }

    @Test
    public void shouldReturnRegimen4_5Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(10).psychiatricIllness(false).lowBaselineHBCount(false);
        assertEquals("Regimen4_5", execute());
    }

    @Test
    public void shouldReturnRegimen4_6Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(10).psychiatricIllness(true).lowBaselineHBCount(true);
        assertEquals("Regimen4_1_6", execute());
    }

    @Test
    public void shouldReturnRegimen4_7Tree() {
        medicalCondition.regimenName("Regimen IV").cd4Count(90).psychiatricIllness(true).lowBaselineHBCount(true);
        assertEquals("Regimen4_7", execute());
    }
}
