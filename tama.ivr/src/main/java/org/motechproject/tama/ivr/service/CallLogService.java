package org.motechproject.tama.ivr.service;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.mapper.CallLogMapper;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class CallLogService {

    public static final String MAX_NUMBER_OF_CALL_LOGS_PER_PAGE = "max.number.of.call.logs.per.page";

    private final AllCallLogs allCallLogs;
    private final KookooCallDetailRecordsService kookooCallDetailRecordsService;
    private final CallLogMapper callDetailRecordMapper;
    private final AllPatients allPatients;
    private Properties properties;

    @Autowired
    public CallLogService(AllCallLogs allCallDetails, KookooCallDetailRecordsService kookooCallDetailRecordsService,
                          CallLogMapper callDetailRecordMapper, AllPatients allPatients, @Qualifier("ivrProperties") Properties properties) {
        this.allCallLogs = allCallDetails;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.callDetailRecordMapper = callDetailRecordMapper;
        this.allPatients = allPatients;
        this.properties = properties;
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
            callLog.clinicId(allPatients.get(patientDocumentId).getClinic_id());
        }
        allCallLogs.add(callLog);
    }

    public List<CallLog> getAll() {
        return allCallLogs.getAll();
    }

    public Integer getTotalNumberOfLogs(DateTime fromDate, DateTime toDate, boolean isAdministrator, String clinicId) {
        if (isAdministrator)
            return allCallLogs.findTotalNumberOfCallLogsForDateRange(fromDate, toDate);
        else
            return allCallLogs.findTotalNumberOfCallLogsForDateRangeAndClinic(fromDate, toDate, clinicId);
    }

    public List<CallLog> getLogsForDateRange(DateTime fromDate, DateTime toDate, boolean isAdministrator, String clinicId, int startIndex) {
        if (isAdministrator)
            return allCallLogs.findCallLogsForDateRange(fromDate, toDate, startIndex, getMaxNumberOfCallLogsPerPage());
        else
            return allCallLogs.findCallLogsForDateRangeAndClinic(fromDate, toDate, clinicId, startIndex, getMaxNumberOfCallLogsPerPage());
    }

    private Integer getMaxNumberOfCallLogsPerPage() {
        return Integer.parseInt(properties.getProperty(MAX_NUMBER_OF_CALL_LOGS_PER_PAGE, "20"));
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