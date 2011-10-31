package org.motechproject.tama.service;

import org.joda.time.LocalDate;
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

    private LocalDate today() {
        return DateUtil.today();
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
        medicalCondition.regimenName("Regimen I").age(60).cd4Count(20).diabetic(true).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen1_3", execute());
    }

    @Test
    public void shouldReturnRegimen1_4Tree() {
        medicalCondition.regimenName("Regimen I").age(60).cd4Count(60).hyperTensic(true).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen1_4", execute());
    }

    @Test
    public void shouldReturnRegimen1_1Tree_Scenario1() {
        medicalCondition.age(45).diabetic(true).artRegimenStartDate(today().minusMonths(7)).regimenName("Regimen I");
        assertEquals("Regimen1_1", execute());
    }

    @Test
    public void whenNotRegimenI() {
        medicalCondition.regimenName("Regimen 9");
        assertEquals(null, execute());
    }

    @Test
    public void shouldReturnRegimen2_1Tree() {
        medicalCondition.regimenName("Regimen II");
        assertEquals("Regimen2_1", execute());
    }

    @Test
    public void shouldReturnRegimen2_2Tree_Diabetic_Only() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(false).diabetic(true).age(60).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen2_2", execute());
    }

    @Test
    public void shouldReturnRegimen2_2Tree_HyperTensic_Only() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(false).hyperTensic(true).age(60).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen2_2", execute());
    }

    @Test
    public void shouldReturnRegimen2_2Tree_NephroToxic_Only() {
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(false).nephrotoxic(true).age(60).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen2_2", execute());
    }

    @Test
    public void shouldReturnRegimen2_3() {
        medicalCondition.regimenName("Regimen II").cd4Count(90).psychiatricIllness(false).nephrotoxic(true).age(60).artRegimenStartDate(today().minusMonths(6));
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
        medicalCondition.regimenName("Regimen II").cd4Count(10).psychiatricIllness(true).diabetic(true).age(60).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen2_6", execute());
    }

    @Test
    public void shouldReturnRegimen2_7() {
        medicalCondition.regimenName("Regimen II").cd4Count(90).psychiatricIllness(true).diabetic(true).age(60).artRegimenStartDate(today().minusMonths(6));
        assertEquals("Regimen2_5_7", execute());
    }

    @Test
    public void shouldReturnRegimen3_1() {
        medicalCondition.regimenName("Regimen III").lowBaselineHBCount(false).cd4Count(10);
        assertEquals("Regimen3_1", execute());
    }

    @Test
    public void shouldReturnRegimen3_2() {
        medicalCondition.regimenName("Regimen III").lowBaselineHBCount(false).cd4Count(100);
        assertEquals("Regimen3_2", execute());
    }

    @Test
    public void shouldReturnRegimen3_3() {
        medicalCondition.regimenName("Regimen III").lowBaselineHBCount(true).cd4Count(10);
        assertEquals("Regimen3_3", execute());
    }

    @Test
    public void shouldReturnRegimen3_4() {
        medicalCondition.regimenName("Regimen III").lowBaselineHBCount(true).cd4Count(90);
        assertEquals("Regimen3_4", execute());
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
