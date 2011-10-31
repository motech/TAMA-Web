package org.motechproject.tama.service.symptomreportingservice;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.MedicalCondition;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.service.SymptomReportingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}
