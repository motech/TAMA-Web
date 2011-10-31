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
public class Regimen3Test extends SpringIntegrationTest {
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
}
