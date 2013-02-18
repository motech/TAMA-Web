package org.motechproject.tama.migration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationMigration0_6To0_7Context.xml")
public class MigrationIntegrationTest {

    @Autowired
    AllPatients allPatients;

    @Autowired
    PatientReportingService patientReportingService;

    @Test
    public void shouldIntegrateWithTAMAWeb() {
        assertNotNull(allPatients);
    }

    @Test
    public void shouldIntegrateWithTAMAReporting() {
        assertNotNull(patientReportingService);
    }
}
