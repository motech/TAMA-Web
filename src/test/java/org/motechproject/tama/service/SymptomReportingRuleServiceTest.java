package org.motechproject.tama.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.PatientStats;
import org.motechproject.tama.domain.VitalStatistics;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")

public class SymptomReportingRuleServiceTest extends SpringIntegrationTest {
    @Autowired
    private SymptomReportingRuleService ruleService;

   @Test
    public void shouldReturnThirtySevenForAppropriateRule(){
        PatientStats statistics = new PatientStats() {{
            setRegimen(4);
            setBaseLineCD4Count(100);
            setBaseLineHB4(100);
            setPychiatricIllnessHistoryPresent(true);
        }};
       Assert.assertNotNull(ruleService.symptomReportingTransition(statistics));
       Assert.assertEquals("37", ruleService.symptomReportingTransition(statistics)) ;
    }

    @Test
    public void shouldReturnThirtyFiveForAppropriateRule(){
        PatientStats statistics = new PatientStats() {{
            setRegimen(4);
            setBaseLineHB4(1);
            setBaseLineCD4Count(54);
        }};
       Assert.assertNotNull(ruleService.symptomReportingTransition(statistics));
       Assert.assertEquals("35", ruleService.symptomReportingTransition(statistics)) ;
    }

    @Test
    public void shouldReturnThirtyFourForAppropriateRule(){
        PatientStats statistics = new PatientStats() {{
            setRegimen(4);
            setBaseLineHB4(1);
            setBaseLineCD4Count(23);
        }};
       Assert.assertNotNull(ruleService.symptomReportingTransition(statistics));
       Assert.assertEquals("34", ruleService.symptomReportingTransition(statistics)) ;
    }

}
