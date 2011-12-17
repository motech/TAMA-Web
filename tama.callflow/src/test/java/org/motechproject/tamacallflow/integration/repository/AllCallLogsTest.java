package org.motechproject.tamacallflow.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tamacallflow.domain.CallLog;
import org.motechproject.tamacallflow.repository.AllCallLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationCallFlowContext.xml", inheritLocations = false)
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
