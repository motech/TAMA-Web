package org.motechproject.tama.service.symptomreportingservice.regimen2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.MedicalConditionBuilder;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:**/applicationContext.xml")
public class Regimen2Test_NoPsychiatricIllness_And_MedicalCondition extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingService symptomReportingService;

    private MedicalCondition medicalConditionsCase1;

    @Before
    public void setUp() {
        medicalConditionsCase1 = MedicalConditionBuilder.startRecording().ForRegimen2().Male().NoHistoryOfPsychiatricIllness().AboveMiddleAge().HistoryOfNephrotoxicMedication().AdviceIsWithin6And12Months().build();
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
        assertEquals("Regimen2_2", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }

    private void assertOnHighCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(60);
        assertEquals("Regimen2_3", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }
}
