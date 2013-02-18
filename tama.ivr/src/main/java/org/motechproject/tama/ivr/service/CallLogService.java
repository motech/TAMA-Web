package org.motechproject.tama.ivr.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.tama.ivr.mapper.CallLogMapper;
import org.motechproject.tama.ivr.reporting.HealthTipsRequestMapper;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.reporting.service.CallLogReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallLogService {

    public static final String MAX_NUMBER_OF_CALL_LOGS_PER_PAGE = "max.number.of.call.logs.per.page";

    private final AllCallLogs allCallLogs;
    private final KookooCallDetailRecordsService kookooCallDetailRecordsService;
    private final CallLogMapper callDetailRecordMapper;
    private final AllPatients allPatients;
    private CallLogReportingService callLogReportingService;

    @Autowired
    public CallLogService(@Qualifier("allCallLogs") AllCallLogs allCallDetails,
                          KookooCallDetailRecordsService kookooCallDetailRecordsService,
                          CallLogMapper callDetailRecordMapper,
                          AllPatients allPatients,
                          CallLogReportingService callLogReportingService) {
        this.allCallLogs = allCallDetails;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.callDetailRecordMapper = callDetailRecordMapper;
        this.allPatients = allPatients;
        this.callLogReportingService = callLogReportingService;
    }

    public void log(String callId, String patientDocumentId) {
        KookooCallDetailRecord kookooCallDetailRecord = kookooCallDetailRecordsService.get(callId);
        CallLog callLog = callDetailRecordMapper.toCallLog(patientDocumentId, kookooCallDetailRecord);
        callLog.maskAuthenticationPin();
        if (patientDocumentId == null) {
            List<String> allLikelyPatientIds = getAllLikelyPatientIds(kookooCallDetailRecord);
            String clinicId = allLikelyPatientIds.isEmpty() ? null : allPatients.get(allLikelyPatientIds.get(0)).getClinic_id();
            callLog.clinicId(clinicId);
            callLog.setLikelyPatientIds(allLikelyPatientIds);
        } else {
            Patient patient = allPatients.get(patientDocumentId);
            callLog.clinicId(patient.getClinic_id());
            callLog.patientId(patient.getPatientId());
            callLog.callLanguage(patient.getLanguageCode());
        }
        allCallLogs.add(callLog);
        callLogReportingService.reportHealthTips(new HealthTipsRequestMapper(callLog).map());
    }

    public List<CallLog> getAll() {
        return allCallLogs.getAll();
    }

    public Integer getTotalNumberOfLogs(CallLogSearch callLogSearch) {
        if (callLogSearch.isSearchByPatientId()) {
            if (callLogSearch.isSearchAllClinics())
                return allCallLogs.findTotalNumberOfCallLogsForDateRangeAndPatientId(callLogSearch);
            else
                return allCallLogs.findTotalNumberOfCallLogsForDateRangePatientIdAndClinic(callLogSearch);
        } else {
            if (callLogSearch.isSearchAllClinics())
                return allCallLogs.findTotalNumberOfCallLogsForDateRange(callLogSearch);
            else
                return allCallLogs.findTotalNumberOfCallLogsForDateRangeAndClinic(callLogSearch);
        }
    }

    public List<CallLog> getLogsForDateRange(CallLogSearch callLogSearch) {
        if (callLogSearch.isSearchByPatientId()) {
            if (callLogSearch.isSearchAllClinics())
                return allCallLogs.findCallLogsForDateRangeAndPatientId(callLogSearch);
            else
                return allCallLogs.findCallLogsForDateRangePatientIdAndClinic(callLogSearch);
        } else {
            if (callLogSearch.isSearchAllClinics())
                return allCallLogs.findCallLogsForDateRange(callLogSearch);
            else
                return allCallLogs.findCallLogsForDateRangeAndClinic(callLogSearch);
        }
    }

    private List<String> getAllLikelyPatientIds(KookooCallDetailRecord kookooCallDetailRecord) {
        String phoneNumber = kookooCallDetailRecord.getCallDetailRecord().getPhoneNumber();
        ArrayList<String> patientIds = new ArrayList<String>();
        for (Patient patient : allPatients.findAllByMobileNumber(phoneNumber)) {
            patientIds.add(patient.getId());
        }
        return patientIds;
    }
}