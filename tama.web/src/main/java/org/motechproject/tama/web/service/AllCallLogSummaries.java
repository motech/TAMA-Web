package org.motechproject.tama.web.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.facility.domain.Clinics;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguages;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.web.builder.CallLogSummaryBuilder;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AllCallLogSummaries {

    private AllCallLogs allCallLogs;
    private CallLogSummaryBuilder callLogSummaryBuilder;

    public AllCallLogSummaries(AllCallLogs allCallLogs, AllPatients allPatients, AllClinics allClinics, AllIVRLanguages allIVRLanguages) {
        this.allCallLogs = allCallLogs;
        this.callLogSummaryBuilder = new CallLogSummaryBuilder(allPatients,
                new Patients(allPatients.getAll()),
                new Clinics(allClinics.getAll()),
                new IVRLanguages(allIVRLanguages.getAll())
        );
    }

    public List<CallLogSummary> getAllCallLogSummariesBetween(LocalDate startDate, LocalDate endDate, int pageNumber, int pageSize) {
        List<CallLogSummary> callLogSummaries = new ArrayList<CallLogSummary>();
        List<CallLog> allCallLogsForDateRange = allCallLogs.findAllCallLogsForDateRange(DateUtil.newDateTime(startDate, 0, 0, 0),
                DateUtil.newDateTime(endDate, 23, 59, 59),
                pageNumber,
                pageSize
        );
        for (CallLog callLog : allCallLogsForDateRange) {
            callLogSummaries.add(callLogSummaryBuilder.build(callLog));
        }
        return callLogSummaries;
    }
}