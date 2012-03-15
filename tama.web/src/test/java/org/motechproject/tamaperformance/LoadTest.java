package org.motechproject.tamaperformance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamaperformance.datasetup.LoadTestSetupService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationDataSetup.xml", inheritLocations = false)
public class LoadTest extends BaseTest {

    @Autowired
    private LoadTestSetupService loadTestSetupService;

    @Before
    public void setUp() {
        loadTestSetupService.createClinicians(1);
        loadTestSetupService.createPatients(DateUtil.today(), 1);
    }

    @Test
    public void shouldDoSomething() {
        System.out.println("some thing");
    }
}
