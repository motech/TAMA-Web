package org.motechproject.tama.facility.integration.repository;

import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.facility.repository.AllMonitoringAgents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "classpath*:applicationFacilityContext.xml", inheritLocations = false)
public class AllMonitoringAgentsIT extends SpringIntegrationTest{

	@Autowired
    private AllMonitoringAgents allMonitoringAgents;
}
