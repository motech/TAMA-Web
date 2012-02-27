package org.motechproject.tama.ivr.integration.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationIVRContext.xml", inheritLocations = false)
public class AllCallLogsIT extends SpringIntegrationTest {
    public static final String PATIENT_DOC_ID = "patientDocId";
    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldFindCallLogByClinicId() {
        createCallLog(DateUtil.now());
        final CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(1), CallLog.CallLogType.Answered, false, "clinicId");
        callLogSearch.setPaginationParams(0, 10);
        assertEquals("clinicId", allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).get(0).clinicId());
    }

    @Test
    public void shouldReturnSpecificNumberOfCallLogs_GivenADateRangeAndLimit() {
        createCallLog(DateUtil.now());
        createCallLog(DateUtil.now().plusDays(1));
        createCallLog(DateUtil.now().plusDays(2));

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, false, "clinicId");
        callLogSearch.setPaginationParams(0, 10);
        assertEquals(3, allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).size());

        callLogSearch = new CallLogSearch(DateUtil.now(), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, true, null);
        callLogSearch.setPaginationParams(0, 2);
        assertEquals(2, allCallLogs.findCallLogsForDateRange(callLogSearch).size());
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

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, false, "clinicId");
        callLogSearch.setPaginationParams(0, 10);
        assertEquals(5, allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).size());

        callLogSearch = new CallLogSearch(DateUtil.now(), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, true, null);
        callLogSearch.setPaginationParams(2, 2);
        List<CallLog> callLogs = allCallLogs.findCallLogsForDateRange(callLogSearch);
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

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, false, "clinicId");
        callLogSearch.setPaginationParams(0, 10);
        assertEquals(5, allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).size());

        callLogSearch = new CallLogSearch(firstDay, thirdDay, CallLog.CallLogType.Answered, true, null);
        int totalNumberOfCallLogs = allCallLogs.findTotalNumberOfCallLogsForDateRange(callLogSearch);
        assertEquals(5, totalNumberOfCallLogs);
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallLogs_GivenDateRangeAndClinic() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        createCallLog(firstDay, "clinic1", "GotDTMF");
        createCallLog(secondDay, "clinic1", "GotDTMF");
        createCallLog(secondDay, "clinic2", "GotDTMF");
        createCallLog(thirdDay, "clinic1", "GotDTMF");
        createCallLog(thirdDay, "clinic2", "GotDTMF");

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, true, null);
        callLogSearch.setPaginationParams(0, 10);
        assertEquals(5, allCallLogs.findCallLogsForDateRange(callLogSearch).size());

        callLogSearch = new CallLogSearch(firstDay, thirdDay, CallLog.CallLogType.Answered, false, "clinic2");
        callLogSearch.setPaginationParams(0, 10);
        int totalNumberOfCallLogs = allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).size();
        assertEquals(2, totalNumberOfCallLogs);
    }

    @Test
    public void shouldListMissedCalls() throws Exception {
        createCallLog(DateUtil.now(), "clinic1", "GotDTMF");
        createCallLog(DateUtil.now(), "clinic1", "Missed");
        createCallLog(DateUtil.now(), "clinic2", "Missed");

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Missed, true, null);
        callLogSearch.setPaginationParams(0, 10);
        final List<CallLog> filteredLogs = allCallLogs.findCallLogsForDateRange(callLogSearch);

        assertEquals(2, filteredLogs.size());
        final List<CallEvent> callEvents = filteredLogs.get(0).getCallEvents();
        assertEquals("Missed", callEvents.get(0).getName());

        int filteredLogSize = allCallLogs.findTotalNumberOfCallLogsForDateRange(callLogSearch);
        assertEquals(2, filteredLogSize);
    }

    @Test
    public void shouldListMissedCalls_ForAClinic() throws Exception {
        createCallLog(DateUtil.now(), "clinic1", "GotDTMF");
        createCallLog(DateUtil.now(), "clinic1", "Missed");
        createCallLog(DateUtil.now(), "clinic2", "Missed");

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Missed, false, "clinic1");
        callLogSearch.setPaginationParams(0, 10);
        final List<CallLog> filteredLogs = allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch);

        assertEquals(1, filteredLogs.size());
        final List<CallEvent> callEvents = filteredLogs.get(0).getCallEvents();
        assertEquals("Missed", callEvents.get(0).getName());

        int filteredLogSize = allCallLogs.findTotalNumberOfCallLogsForDateRangeAndClinic(callLogSearch);
        assertEquals(1, filteredLogSize);
    }

    private CallLog createCallLog(DateTime startTime) {
        return createCallLog(startTime, "clinicId", "GotDTMF");
    }
    
    private CallLog createCallLog(DateTime startTime, String clinic, String callEventType) {
        CallLog callLog = new CallLog(PATIENT_DOC_ID);
        callLog.setStartTime(startTime);
        callLog.clinicId(clinic);
        callLog.setEndTime(startTime.plusMinutes(5));

        List<CallEvent> callEventList = new ArrayList<CallEvent>();
        callEventList.add(new CallEvent(callEventType));
        callLog.setCallEvents(callEventList);

        allCallLogs.add(callLog);
        markForDeletion(callLog);
        return callLog;
    }
}
