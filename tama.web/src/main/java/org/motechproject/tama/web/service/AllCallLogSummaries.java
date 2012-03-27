package org.motechproject.tama.web.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.web.builder.CallLogSummaryBuilder;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AllCallLogSummaries {

    private AllCallLogs allCallLogs;
    private CallLogSummaryBuilder callLogSummaryBuilder;

    public AllCallLogSummaries(AllCallLogs allCallLogs, AllPatients allPatients, AllIVRLanguagesCache allIVRLanguages) {
        this.allCallLogs = allCallLogs;
        this.callLogSummaryBuilder = new CallLogSummaryBuilder(allPatients, new Patients(allPatients.getAll()), allIVRLanguages);
    }

    public List<CallLog> getAllCallLogSummariesBetween(DateTime startDate, String startDocId, DateTime endDate, int pageSize) {
        List<CallLog> allCallLogsForDateRange = allCallLogs.findAllCallLogsForDateRange(startDate, startDocId, endDate, pageSize);
        return allCallLogsForDateRange;
    }

    public List<CallLogSummary> getSummariesFor(List<CallLog> callLogs) {
        List<CallLogSummary> callLogSummaries = new ArrayList<CallLogSummary>();
        for (CallLog callLog : callLogs) {
            callLogSummaries.add(callLogSummaryBuilder.build(callLog));
        }
        return callLogSummaries;
    }
}