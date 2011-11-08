package org.motechproject.tama.integration.symptomreportingservice.regimen5;

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
public class Regimen5Test_MaleOrPatientWithLowBMI_And_MedicalCondition extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingService symptomReportingService;

    private MedicalCondition medicalConditionsCase1;
    private MedicalCondition medicalConditionsCase2;
    private MedicalCondition medicalConditionsCase3;

    @Before
    public void setUp() {
        medicalConditionsCase1 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().HighBMI().HistoryOfTuberculosis().AdviceIsWithin6And12Months().build();
        medicalConditionsCase2 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().LowBMI().HistoryOfTuberculosis().AdviceIsWithin6And12Months().build();
        medicalConditionsCase3 = MedicalConditionBuilder.startRecording().ForRegimen5().Female().LowBMI().HistoryOfTuberculosis().AdviceIsWithin6And12Months().build();
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
        assertEquals("Regimen5_1", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }

    private void assertOnHighCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(60);
        assertEquals("Regimen5_2_3_5_7", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }
}
