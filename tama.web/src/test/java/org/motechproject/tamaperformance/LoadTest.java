package org.motechproject.tamaperformance;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamaperformance.datasetup.LoadTestSetupService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationDataSetup.xml", inheritLocations = false)
public class LoadTest extends BaseIVRTest {

    @Autowired
    private LoadTestSetupService loadTestSetupService;
    private TAMADateTimeService tamaDateTimeService;

    @Before
    public void setup() {
        super.setUp();
        DateTime startDate = DateUtil.now().minusYears(1);
        tamaDateTimeService = new TAMADateTimeService(webClient);
        loadTestSetupService.createClinicians(6);
        loadTestSetupService.createPatients(startDate.toLocalDate(), 300);
        loadTestSetupService.createCallLogs(tamaDateTimeService, webClient, startDate, 300);
    }

    @Test
    public void shouldDoSomething() {
        System.out.println("some thing");
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }
}
