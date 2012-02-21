package org.motechproject.tama.ivr.integration.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationIVRContext.xml", inheritLocations = false)
public class AllCallLogsTest extends SpringIntegrationTest {
    public static final String PATIENT_DOC_ID = "patientDocId";
    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldFindCallLogByClinicId() {
        createCallLog(DateUtil.now());
        assertEquals("clinicId", allCallLogs.findCallLogsForDateRangeAndClinic(DateUtil.now().minusDays(1), DateUtil.now().plusDays(1), "clinicId", 0, 10).get(0).clinicId());
    }

    @Test
    public void shouldReturnSpecificNumberOfCallLogs_GivenADateRangeAndLimit() {
        createCallLog(DateUtil.now());
        createCallLog(DateUtil.now().plusDays(1));
        createCallLog(DateUtil.now().plusDays(2));

        assertEquals(3, allCallLogs.findCallLogsForDateRangeAndClinic(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), "clinicId", 0, 10).size());

        assertEquals(2, allCallLogs.findCallLogsForDateRange(DateUtil.now(), DateUtil.now().plusDays(3), 0, 2).size());
    }

    @Test
    public void shouldReturnCallLogsFromParticularIndex_GivenADateRangeAndIndex() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        createCallLog(firstDay);
        createCallLog(secondDay);
        createCallLog(secondDay);
        createCallLog(thirdDay);
        createCallLog(thirdDay);

        assertEquals(5, allCallLogs.findCallLogsForDateRangeAndClinic(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), "clinicId", 0, 10).size());

        List<CallLog> callLogs = allCallLogs.findCallLogsForDateRange(DateUtil.now(), DateUtil.now().plusDays(3), 2, 2);
        assertEquals(2, callLogs.size());
        assertEquals(thirdDay.toLocalDate(), callLogs.get(0).getStartTime().toLocalDate());
        assertEquals(thirdDay.toLocalDate(), callLogs.get(1).getStartTime().toLocalDate());
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallLogs_GivenADateRange() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        createCallLog(firstDay);
        createCallLog(secondDay);
        createCallLog(secondDay);
        createCallLog(thirdDay);
        createCallLog(thirdDay);

        assertEquals(5, allCallLogs.findCallLogsForDateRangeAndClinic(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), "clinicId", 0, 10).size());
        int totalNumberOfCallLogs = allCallLogs.findTotalNumberOfCallLogsForDateRange(firstDay, thirdDay);
        assertEquals(5, totalNumberOfCallLogs);
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallLogs_GivenDateRangeAndClinic() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        createCallLog(firstDay, "clinic1");
        createCallLog(secondDay, "clinic1");
        createCallLog(secondDay, "clinic2");
        createCallLog(thirdDay, "clinic1");
        createCallLog(thirdDay, "clinic2");

        assertEquals(5, allCallLogs.findCallLogsForDateRange(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), 0, 10).size());

        int totalNumberOfCallLogs = allCallLogs.findTotalNumberOfCallLogsForDateRangeAndClinic(firstDay, thirdDay, "clinic2");
        assertEquals(2, totalNumberOfCallLogs);
    }



    private CallLog createCallLog(DateTime startTime) {
        return createCallLog(startTime, "clinicId");
    }
    
    private CallLog createCallLog(DateTime startTime, String clinic) {
        CallLog callLog = new CallLog(PATIENT_DOC_ID);
        callLog.setStartTime(startTime);
        callLog.clinicId(clinic);
        callLog.setEndTime(startTime.plusMinutes(5));
        allCallLogs.add(callLog);
        markForDeletion(callLog);
        return callLog;
    }
}
