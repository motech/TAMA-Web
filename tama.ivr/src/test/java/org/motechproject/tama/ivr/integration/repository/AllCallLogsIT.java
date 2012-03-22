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
    public static final String PATIENT_ID1 = "patientId1";
    public static final String PATIENT_ID2 = "patientId2";
    @Autowired
    private AllCallLogs allCallLogs;

    @Test
    public void shouldFindCallLogByClinicId() {
        createCallLog(DateUtil.now());
        final CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(1), CallLog.CallLogType.Answered, PATIENT_ID1, false, "clinicId");
        callLogSearch.setPaginationParams(0, 10);
        assertEquals("clinicId", allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).get(0).clinicId());
    }

    @Test
    public void shouldReturnSpecificNumberOfCallLogs_GivenADateRangeAndLimit() {
        createCallLog(DateUtil.now());
        createCallLog(DateUtil.now().plusDays(1));
        createCallLog(DateUtil.now().plusDays(2));

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now(), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, null, true, null);
        callLogSearch.setPaginationParams(0, 2);
        assertEquals(2, allCallLogs.findCallLogsForDateRange(callLogSearch).size());
    }

    @Test
    public void shouldFetchTheFirstPage() {
        int firstPageIndex = 0;
        int pageSize = 1;

        DateTime now = DateUtil.now();
        createCallLog(now);
        createCallLog(now.plusDays(1));
        createCallLog(now.plusDays(2));

        assertEquals(pageSize, allCallLogs.findAllCallLogsForDateRange(now, now.plusDays(3), firstPageIndex, pageSize).size());
    }

    @Test
    public void shouldSkipTheFirstPageWhenQueryingForTheSecondPage() {
        DateTime now = DateUtil.now();
        createCallLog(now);
        CallLog callLogOnSecondPage = createCallLog(now.plusDays(1));
        createCallLog(now.plusDays(2));

        assertEquals(callLogOnSecondPage.getId(), allCallLogs.findAllCallLogsForDateRange(now, now.plusDays(3), 1, 1).get(0).getId());
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

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now(), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, PATIENT_ID1, true, null);
        callLogSearch.setPaginationParams(2, 2);
        List<CallLog> callLogs = allCallLogs.findCallLogsForDateRange(callLogSearch);
        assertEquals(2, callLogs.size());
        assertEquals(thirdDay.toLocalDate(), callLogs.get(0).getStartTime().toLocalDate());
        assertEquals(thirdDay.toLocalDate(), callLogs.get(1).getStartTime().toLocalDate());
    }

    @Test
    public void shouldFindAllCallLogsInAGivenDateRange() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        createCallLog(firstDay, "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(secondDay, "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(secondDay, "clinic2", "GotDTMF", PATIENT_ID1);
        createCallLog(thirdDay, "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(thirdDay, "clinic2", "GotDTMF", PATIENT_ID1);

        List<CallLog> allCallLogsForDateRange = allCallLogs.findAllCallLogsForDateRange(firstDay, thirdDay, 0, 10);
        assertEquals(5, allCallLogsForDateRange.size());
        assertEquals("clinic1", allCallLogsForDateRange.get(0).clinicId());
        assertEquals("clinic2", allCallLogsForDateRange.get(2).clinicId());
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

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, null, true, null);
        int totalNumberOfCallLogs = allCallLogs.findTotalNumberOfCallLogsForDateRange(callLogSearch);
        assertEquals(5, totalNumberOfCallLogs);
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallLogs_GivenDateRangeAndClinic() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        createCallLog(firstDay, "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(secondDay, "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(secondDay, "clinic2", "GotDTMF", PATIENT_ID1);
        createCallLog(thirdDay, "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(thirdDay, "clinic2", "GotDTMF", PATIENT_ID1);

        CallLogSearch callLogSearch = new CallLogSearch(firstDay, thirdDay, CallLog.CallLogType.Answered, null, false, "clinic2");
        callLogSearch.setPaginationParams(0, 10);
        int totalNumberOfCallLogs = allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch).size();
        assertEquals(2, totalNumberOfCallLogs);
    }

    @Test
    public void shouldListMissedCalls() throws Exception {
        createCallLog(DateUtil.now(), "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(DateUtil.now(), "clinic1", "Missed", PATIENT_ID1);
        createCallLog(DateUtil.now(), "clinic2", "Missed", PATIENT_ID1);

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Missed, null, true, null);
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
        createCallLog(DateUtil.now(), "clinic1", "GotDTMF", PATIENT_ID1);
        createCallLog(DateUtil.now(), "clinic1", "Missed", PATIENT_ID1);
        createCallLog(DateUtil.now(), "clinic2", "Missed", PATIENT_ID1);

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Missed, null, false, "clinic1");
        callLogSearch.setPaginationParams(0, 10);
        final List<CallLog> filteredLogs = allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch);

        assertEquals(1, filteredLogs.size());
        final List<CallEvent> callEvents = filteredLogs.get(0).getCallEvents();
        assertEquals("Missed", callEvents.get(0).getName());

        int filteredLogSize = allCallLogs.findTotalNumberOfCallLogsForDateRangeAndClinic(callLogSearch);
        assertEquals(1, filteredLogSize);
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallLogs_GivenADateRangeAndPatientId() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        CallLog callLog1 = createCallLog(firstDay, PATIENT_ID1);
        CallLog callLog2 = createCallLog(secondDay, PATIENT_ID1);
        CallLog callLog3 = createCallLog(secondDay, PATIENT_ID2);
        CallLog callLog4 = createCallLog(thirdDay, PATIENT_ID1);
        CallLog callLog5 = createCallLog(thirdDay, PATIENT_ID2);

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, PATIENT_ID1.toLowerCase(), true, null);
        callLogSearch.setPaginationParams(0, 20);
        int totalNumberOfCallLogs = allCallLogs.findTotalNumberOfCallLogsForDateRangeAndPatientId(callLogSearch);
        assertEquals(3, totalNumberOfCallLogs);

        List<CallLog> expectedCallLogs = allCallLogs.findCallLogsForDateRangeAndPatientId(callLogSearch);

        assertEquals(callLog1, expectedCallLogs.get(0));
        assertEquals(callLog2, expectedCallLogs.get(1));
        assertEquals(callLog4, expectedCallLogs.get(2));
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallLogs_GivenADateRange_AndPatientId_AndAClinicId() {
        DateTime firstDay = DateUtil.now();
        DateTime secondDay = DateUtil.now().plusDays(1);
        DateTime thirdDay = DateUtil.now().plusDays(2);

        CallLog callLog1 = createCallLog(firstDay, "clinic1", "Newcall", PATIENT_ID1);
        CallLog callLog2 = createCallLog(secondDay, "clinic1", "GotDTMF", PATIENT_ID1);
        CallLog callLog3 = createCallLog(secondDay, "clinic2", "Newcall", PATIENT_ID2);
        CallLog callLog4 = createCallLog(thirdDay, "clinic1", "GotDTMF", PATIENT_ID1);
        CallLog callLog5 = createCallLog(thirdDay, "clinic2", "GotDTMF", PATIENT_ID2);
        CallLog callLog6 = createCallLog(thirdDay, "clinic1", "Newcall", PATIENT_ID2);

        CallLogSearch callLogSearch = new CallLogSearch(DateUtil.now().minusDays(1), DateUtil.now().plusDays(3), CallLog.CallLogType.Answered, PATIENT_ID1.toLowerCase(), false, "clinic1");
        callLogSearch.setPaginationParams(0, 20);
        int totalNumberOfCallLogs = allCallLogs.findTotalNumberOfCallLogsForDateRangePatientIdAndClinic(callLogSearch);
        assertEquals(3, totalNumberOfCallLogs);

        List<CallLog> expectedCallLogs = allCallLogs.findCallLogsForDateRangePatientIdAndClinic(callLogSearch);

        assertEquals(callLog1, expectedCallLogs.get(0));
        assertEquals(callLog2, expectedCallLogs.get(1));
        assertEquals(callLog4, expectedCallLogs.get(2));
    }

    private CallLog createCallLog(DateTime startTime) {
        return createCallLog(startTime, "clinicId", "GotDTMF", PATIENT_ID1);
    }

    private CallLog createCallLog(DateTime startTime, String patientId) {
        return createCallLog(startTime, "clinicId", "GotDTMF", patientId);
    }

    private CallLog createCallLog(DateTime startTime, String clinic, String callEventType, String patientId) {
        CallLog callLog = new CallLog();
        callLog.patientId(patientId);
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
