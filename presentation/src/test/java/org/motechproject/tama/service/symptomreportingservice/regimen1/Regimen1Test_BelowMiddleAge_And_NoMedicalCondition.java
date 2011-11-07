package org.motechproject.tama.service.symptomreportingservice.regimen1;

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
public class Regimen1Test_BelowMiddleAge_And_NoMedicalCondition extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingService symptomReportingService;

    private MedicalCondition medicalConditionsCase1;
    private MedicalCondition medicalConditionsCase2;
    private MedicalCondition medicalConditionsCase3;

    @Before
    public void setUp() {
        medicalConditionsCase1 = MedicalConditionBuilder.startRecording().ForRegimen1().Male().BelowMiddleAge().NoHistoryOfMedicalConditions().AdviceIsWithin6And12Months().build();
        medicalConditionsCase2 = MedicalConditionBuilder.startRecording().ForRegimen1().Male().BelowMiddleAge().HistoryOfDiabetes().AdviceIsWithin6Months().build();
        medicalConditionsCase3 = MedicalConditionBuilder.startRecording().ForRegimen1().Male().BelowMiddleAge().NoHistoryOfMedicalConditions().AdviceIsWithin6Months().build();
    }

    @Test
    public void patientHasLowCD4Count() {
        assertOnLowCD4Count(medicalConditionsCase1);
        assertOnLowCD4Count(medicalConditionsCase2);
        assertOnLowCD4Count(medicalConditionsCase3);
    }

    @Test
    public void patientHasHighCD4Count() {
        assertOnHighCD4Count(medicalConditionsCase1);
        assertOnHighCD4Count(medicalConditionsCase2);
        assertOnHighCD4Count(medicalConditionsCase3);
    }

    private void assertOnLowCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(10);
        assertEquals("Regimen1_1", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }

    private void assertOnHighCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(60);
        assertEquals("Regimen1_2", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }
}
