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
public class Regimen5Test extends SpringIntegrationTest {
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
    public void shouldReturnRegimen5_1Tree() {
        medicalCondition.regimenName("Regimen V").alcoholic(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20);
        assertEquals("Regimen5_1", execute());
    }

    @Test
    public void shouldReturnRegimen5_2Tree() {
        medicalCondition.regimenName("Regimen V").alcoholic(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(60);
        assertEquals("Regimen5_2_3_5_7", execute());
    }

    @Test
    public void shouldReturnRegimen5_3Tree() {
        medicalCondition.regimenName("Regimen V");
        assertEquals("Regimen5_2_3_5_7", execute());
    }

    @Test
    public void shouldReturnRegimen5_4Tree() {
        medicalCondition.regimenName("Regimen V").gender("Female").bmi(27.1).cd4Count(10);
        assertEquals("Regimen5_4", execute());
    }

    @Test
    public void shouldReturnRegimen5_5Tree() {
        medicalCondition.regimenName("Regimen V").gender("Female").bmi(27.1).cd4Count(60);
        assertEquals("Regimen5_2_3_5_7", execute());
    }

    @Test
    public void shouldReturnRegimen5_6Tree() {
        medicalCondition.regimenName("Regimen V").tuberculosis(true).artRegimenStartDate(today().minusMonths(12)).gender("Female").bmi(28).cd4Count(10);
        assertEquals("Regimen5_6", execute());
    }

    @Test
    public void shouldReturnRegimen5_7Tree() {
        medicalCondition.regimenName("Regimen V").tuberculosis(true).artRegimenStartDate(today().minusMonths(12)).gender("Female").bmi(28).cd4Count(60);
        assertEquals("Regimen5_2_3_5_7", execute());
    }
}
