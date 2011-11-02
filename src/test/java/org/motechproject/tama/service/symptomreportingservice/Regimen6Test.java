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
public class Regimen6Test extends SpringIntegrationTest {
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
    public void shouldReturnRegimen6_1Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(30);
        assertEquals("Regimen6_1_2", execute());
    }

    @Test
    public void shouldReturnRegimen6_2Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").diabetic(true).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(30);
        assertEquals("Regimen6_1_2", execute());
    }

    @Test
    public void shouldReturnRegimen6_3Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Male").diabetic(true).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20);
        assertEquals("Regimen6_3", execute());
    }

    @Test
    public void shouldReturnRegimen6_3Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").diabetic(true).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(20);
        assertEquals("Regimen6_3", execute());
    }

    @Test
    public void shouldReturnRegimen6_4Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").diabetic(true).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(20);
        assertEquals("Regimen6_4", execute());
    }

    @Test
    public void shouldReturnRegimen6_4Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Male").diabetic(true).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(20);
        assertEquals("Regimen6_4", execute());
    }

    @Test
    public void shouldReturnRegimen6_5Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(40);
        assertEquals("Regimen6_5", execute());
    }

    @Test
    public void shouldReturnRegimen6_6Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(40);
        assertEquals("Regimen6_6", execute());
    }

    @Test
    public void shouldReturnRegimen6_7Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(28);
        assertEquals("Regimen6_7_8", execute());
    }

    @Test
    public void shouldReturnRegimen6_7Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(true).cd4Count(20).bmi(28).artRegimenStartDate(today());
        assertEquals("Regimen6_7_8", execute());
    }

    @Test
    public void shouldReturnRegimen6_8Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(28);
        assertEquals("Regimen6_7_8", execute());
    }

    @Test
    public void shouldReturnRegimen6_8Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(true).cd4Count(60).bmi(28).artRegimenStartDate(today());
        assertEquals("Regimen6_7_8", execute());
    }

    @Test
    public void shouldReturnRegimen6_9Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(20);
        assertEquals("Regimen6_9", execute());
    }

    @Test
    public void shouldReturnRegimen6_9Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(20);
        assertEquals("Regimen6_9", execute());
    }

    @Test
    public void shouldReturnRegimen6_9Tree_Case3() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(true).artRegimenStartDate(today()).cd4Count(20).bmi(20);
        assertEquals("Regimen6_9", execute());
    }

    @Test
    public void shouldReturnRegimen6_9Tree_Case4() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today()).cd4Count(20).bmi(20);
        assertEquals("Regimen6_9", execute());
    }

    @Test
    public void shouldReturnRegimen6_10Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(20);
        assertEquals("Regimen6_10", execute());
    }

    @Test
    public void shouldReturnRegimen6_10Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today().minusMonths(7)).cd4Count(90);
        assertEquals("Regimen6_10", execute());
    }

    @Test
    public void shouldReturnRegimen6_10Tree_Case3() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(true).artRegimenStartDate(today()).cd4Count(70).bmi(20);
        assertEquals("Regimen6_10", execute());
    }

    @Test
    public void shouldReturnRegimen6_10Tree_Case4() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(false).psychiatricIllness(true).artRegimenStartDate(today()).cd4Count(100).bmi(20);
        assertEquals("Regimen6_10", execute());
    }

    @Test
    public void shouldReturnRegimen6_11Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(20);
        assertEquals("Regimen6_11", execute());
    }

    @Test
    public void shouldReturnRegimen6_11Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(20);
        assertEquals("Regimen6_11", execute());
    }

    @Test
    public void shouldReturnRegimen6_11Tree_Case3() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(20);
        assertEquals("Regimen6_11", execute());
    }

    @Test
    public void shouldReturnRegimen6_12Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(600).bmi(20);
        assertEquals("Regimen6_12", execute());
    }

    @Test
    public void shouldReturnRegimen6_12Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(500);
        assertEquals("Regimen6_12", execute());
    }

    @Test
    public void shouldReturnRegimen6_12Tree_Case3() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(60);
        assertEquals("Regimen6_12", execute());
    }

    @Test
    public void shouldReturnRegimen6_13Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(20).bmi(28);
        assertEquals("Regimen6_13", execute());
    }

    @Test
    public void shouldReturnRegimen6_13Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today()).cd4Count(20).bmi(28);
        assertEquals("Regimen6_13", execute());
    }

    @Test
    public void shouldReturnRegimen6_14Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(600).bmi(28);
        assertEquals("Regimen6_14", execute());
    }

    @Test
    public void shouldReturnRegimen6_14Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today()).cd4Count(6000).bmi(28);
        assertEquals("Regimen6_14", execute());
    }

    @Test
    public void shouldReturnRegimen6_15Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(6).bmi(20);
        assertEquals("Regimen6_15_16", execute());
    }

    @Test
    public void shouldReturnRegimen6_15Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today()).cd4Count(10).bmi(20);
        assertEquals("Regimen6_15_16", execute());
    }

    @Test
    public void shouldReturnRegimen6_15Tree_Case3() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today()).cd4Count(10);
        assertEquals("Regimen6_15_16", execute());
    }

    @Test
    public void shouldReturnRegimen6_16Tree_Case1() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(false).psychiatricIllness(false).artRegimenStartDate(today().minusMonths(7)).cd4Count(60).bmi(20);
        assertEquals("Regimen6_15_16", execute());
    }

    @Test
    public void shouldReturnRegimen6_16Tree_Case2() {
        medicalCondition.regimenName("Regimen VI").gender("Female").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today()).cd4Count(100).bmi(20);
        assertEquals("Regimen6_15_16", execute());
    }

    @Test
    public void shouldReturnRegimen6_16Tree_Case3() {
        medicalCondition.regimenName("Regimen VI").gender("Male").alcoholic(true).psychiatricIllness(false).artRegimenStartDate(today()).cd4Count(100);
        assertEquals("Regimen6_15_16", execute());
    }
}
