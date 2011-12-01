package org.motechproject.tamadomain.integration.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.CallLog;
import org.motechproject.tamadomain.repository.AllCallLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

public class AllCallLogsTest extends SpringIntegrationTest {
    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldFindCallLogByClinicId() {
        CallLog callLog = new CallLog("patientDocId");
        callLog.clinicId("clinicId");
        callLog.setStartTime(DateUtil.now());
        callLog.setEndTime(DateUtil.now().plusMinutes(5));
        allCallLogs.add(callLog);

        assertEquals("clinicId", allCallLogs.findByClinic(DateUtil.now().minusDays(1), DateUtil.now().plusDays(1), "clinicId").get(0).clinicId());
        markForDeletion(callLog);
    }
}
