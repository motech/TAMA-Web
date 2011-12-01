package org.motechproject.tamafunctional.frameworkunittest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.testdataservice.ScheduledJobDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class ScheduledJobDateServiceTest {
    @Autowired
    private ScheduledJobDataService service;

    @Test
    public void clearJobsShouldNotThrowException() {
        service.clearJobs();
    }
}
