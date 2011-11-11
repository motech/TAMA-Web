package org.motechproject.tama.integration.symptomreportingservice.regimen5;

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
public class Regimen5Test_MaleOrPatientWithLowBMI_And_NoMedicalCondition extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingTreeService symptomReportingService;

    private MedicalCondition medicalConditionsCase1;
    private MedicalCondition medicalConditionsCase2;
    private MedicalCondition medicalConditionsCase3;
    private MedicalCondition medicalConditionsCase4;
    private MedicalCondition medicalConditionsCase5;
    private MedicalCondition medicalConditionsCase6;
    private MedicalCondition medicalConditionsCase7;
    private MedicalCondition medicalConditionsCase8;
    private MedicalCondition medicalConditionsCase9;

    @Before
    public void setUp() {
        medicalConditionsCase1 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().HighBMI().NoHistoryOfMedicalConditions().AdviceIsWithin6And12Months().build();
        medicalConditionsCase2 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().LowBMI().NoHistoryOfMedicalConditions().AdviceIsWithin6And12Months().build();
        medicalConditionsCase3 = MedicalConditionBuilder.startRecording().ForRegimen5().Female().LowBMI().NoHistoryOfMedicalConditions().AdviceIsWithin6And12Months().build();

        medicalConditionsCase4 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().HighBMI().HistoryOfTuberculosis().AdviceIsWithin6Months().build();
        medicalConditionsCase5 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().LowBMI().HistoryOfTuberculosis().AdviceIsWithin6Months().build();
        medicalConditionsCase6 = MedicalConditionBuilder.startRecording().ForRegimen5().Female().LowBMI().HistoryOfTuberculosis().AdviceIsWithin6Months().build();

        medicalConditionsCase7 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().HighBMI().NoHistoryOfMedicalConditions().AdviceIsWithin6Months().build();
        medicalConditionsCase8 = MedicalConditionBuilder.startRecording().ForRegimen5().Male().LowBMI().NoHistoryOfMedicalConditions().AdviceIsWithin6Months().build();
        medicalConditionsCase9 = MedicalConditionBuilder.startRecording().ForRegimen5().Female().LowBMI().NoHistoryOfMedicalConditions().AdviceIsWithin6Months().build();
    }

    @Test
    public void patientHasLowCD4Count() {
        assertOnLowCD4Count(medicalConditionsCase1);
        assertOnLowCD4Count(medicalConditionsCase2);
        assertOnLowCD4Count(medicalConditionsCase3);
        assertOnLowCD4Count(medicalConditionsCase4);
        assertOnLowCD4Count(medicalConditionsCase5);
        assertOnLowCD4Count(medicalConditionsCase6);
        assertOnLowCD4Count(medicalConditionsCase7);
        assertOnLowCD4Count(medicalConditionsCase8);
        assertOnLowCD4Count(medicalConditionsCase9);
    }

    @Test
    public void patientHasHighCD4Count() {
        assertOnHighCD4Count(medicalConditionsCase1);
        assertOnHighCD4Count(medicalConditionsCase2);
        assertOnHighCD4Count(medicalConditionsCase3);
        assertOnHighCD4Count(medicalConditionsCase4);
        assertOnHighCD4Count(medicalConditionsCase5);
        assertOnHighCD4Count(medicalConditionsCase6);
        assertOnHighCD4Count(medicalConditionsCase7);
        assertOnHighCD4Count(medicalConditionsCase8);
        assertOnHighCD4Count(medicalConditionsCase9);
    }

    private void assertOnLowCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(10);
        assertEquals("Regimen5_2_3_5_7", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }

    private void assertOnHighCD4Count(MedicalCondition medicalCondition) {
        medicalCondition.cd4Count(60);
        assertEquals("Regimen5_2_3_5_7", symptomReportingService.getSymptomReportingTree(medicalCondition));
    }
}
