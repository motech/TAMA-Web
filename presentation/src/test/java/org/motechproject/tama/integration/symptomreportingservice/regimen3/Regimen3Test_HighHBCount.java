package org.motechproject.tama.integration.symptomreportingservice.regimen3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.MedicalConditionBuilder;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.service.SymptomReportingTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:**/applicationContext.xml")
public class Regimen3Test_HighHBCount extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingTreeService symptomReportingService;

    private MedicalCondition medicalConditionsCase1;

    @Before
    public void setUp() {
        medicalConditionsCase1 = MedicalConditionBuilder.startRecording().ForRegimen3().Male().HighBaselineHBCount().build();
    }

    @Test
    public void patientHasLowCD4Count() {
        assertOnLowCD4Count(medicalConditionsCase1);
    }

    @Test
    public void patientHasHighCD4Count() {
        assertOnHighCD4Count(medicalConditionsCase1);
    }

    private void assertOnLowCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(10);
        assertEquals("Regimen3_3", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }

    private void assertOnHighCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(60);
        assertEquals("Regimen3_4", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }
}
