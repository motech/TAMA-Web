package org.motechproject.tama.web.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.web.builder.CallLogSummaryBuilder;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllCallLogSummaries {

    public static final int THRESHOLD = 30000;

    private AllCallLogs allCallLogs;
    private AllPatients allPatients;
    private AllClinics allClinics;
    private AllIVRLanguages allIVRLanguages;

    @Autowired
    public AllCallLogSummaries(AllCallLogs allCallLogs, AllPatients allPatients, AllClinics allClinics, AllIVRLanguages allIVRLanguages) {
        this.allCallLogs = allCallLogs;
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.allIVRLanguages = allIVRLanguages;
    }

    public List<CallLogSummary> getAllCallLogSummariesBetween(LocalDate startDate, LocalDate endDate) {
        List<CallLogSummary> callLogSummaries = new ArrayList<CallLogSummary>();
        List<CallLog> allCallLogsForDateRange = allCallLogs.findAllCallLogsForDateRange(DateUtil.newDateTime(startDate, 0, 0, 0), DateUtil.newDateTime(endDate, 23, 59, 59));
        CallLogSummaryBuilder callLogSummaryBuilder = new CallLogSummaryBuilder(allPatients, allClinics, allIVRLanguages);
        for (CallLog callLog : allCallLogsForDateRange) {
            callLogSummaries.add(callLogSummaryBuilder.build(callLog));
        }
        return callLogSummaries;
    }

    public boolean isNumberOfCallSummariesWithinThreshold(LocalDate startDate, LocalDate endDate) {
        return allCallLogs.countLogsBetween(DateUtil.newDateTime(startDate, 0, 0, 0), DateUtil.newDateTime(endDate, 23, 59, 59)) < THRESHOLD;
    }
}