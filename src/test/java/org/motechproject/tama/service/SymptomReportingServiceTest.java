package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.MedicalCondition;
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
    private MedicalCondition medicalCondition;

    @Before
    public void setUp() {
        medicalCondition = new MedicalCondition();
    }

    private String execute() {
        return symptomReportingService.getSymptomReportingTree(medicalCondition);
    }

    @Test
    public void shouldReturnRegimen1_1Tree() {
        medicalCondition.regimenName("Regimen I").age(20).cd4Count(10);
        assertEquals("Regimen1_1", execute());
    }

    @Test
    public void shouldReturnRegimen1_2Tree() {
        medicalCondition.regimenName("Regimen I").age(20).cd4Count(60);
        assertEquals("Regimen1_2", execute());
    }

    @Test
    public void shouldReturnRegimen1_3Tree() {
        medicalCondition.regimenName("Regimen I").age(60).cd4Count(20).diabetic(true).artRegimenStartDate(DateUtil.today().minusMonths(6));
        assertEquals("Regimen1_3", execute());
    }

    @Test
    public void shouldReturnRegimen1_4Tree() {
        medicalCondition.regimenName("Regimen I").age(60).cd4Count(60).hyperTensic(true).artRegimenStartDate(DateUtil.today().minusMonths(6));
        assertEquals("Regimen1_4", execute());
    }

    @Test
    public void whenNotRegimenI() {
        medicalCondition.regimenName("Regimen 9");
        assertEquals(null, execute());
    }
}
