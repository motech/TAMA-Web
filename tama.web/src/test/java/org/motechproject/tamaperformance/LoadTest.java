package org.motechproject.tamaperformance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamaperformance.datasetup.CreatePatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationDataSetup.xml", inheritLocations = false)
public class LoadTest extends BaseTest {

    @Autowired
    private CreatePatients createPatients;

    @Before
    public void setUp() {
        createPatients.createClinicians(1);
        createPatients.createPatients(DateUtil.today(), 100);
    }

    @Test
    public void shouldDoSomething() {
        System.out.println("some thing");
    }
}
