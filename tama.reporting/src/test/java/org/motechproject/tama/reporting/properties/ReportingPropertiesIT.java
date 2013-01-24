package org.motechproject.tama.reporting.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationReportingContext.xml")
public class ReportingPropertiesIT {

    @Autowired
    private ReportingProperties reportingProperties;

    @Test
    public void shouldGetReportingURL() {
        assertEquals("http://localhost:9999/", reportingProperties.reportingURL());
    }
}
