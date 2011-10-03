package org.motechproject.tama.ivr.logging.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class AllCallLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldFindCallLogByClinicId() {
        CallLog callLog = new CallLog("patientDocId");
        callLog.setClinicId("clinicId");
        allCallLogs.add(callLog);

        assertEquals("clinicId", allCallLogs.findByClinic("clinicId").get(0).getClinicId());
        markForDeletion(callLog);
    }
}
