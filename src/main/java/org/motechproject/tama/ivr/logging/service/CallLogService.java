package org.motechproject.tama.ivr.logging.service;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.mapper.CallLogMapper;
import org.motechproject.tama.ivr.logging.repository.AllCallLogs;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CallLogService {
    private final AllCallLogs allCallLogs;
    private final KookooCallDetailRecordsService kookooCallDetailRecordsService;
    private final CallLogMapper callDetailRecordMapper;
    private final AllPatients allPatients;

    @Autowired
    public CallLogService(AllCallLogs allCallDetails, KookooCallDetailRecordsService kookooCallDetailRecordsService,
                          CallLogMapper callDetailRecordMapper, AllPatients allPatients) {
        this.allCallLogs = allCallDetails;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.callDetailRecordMapper = callDetailRecordMapper;
        this.allPatients = allPatients;
    }

    public void log(String callId, String patientDocumentId) {
        KookooCallDetailRecord kookooCallDetailRecord = kookooCallDetailRecordsService.get(callId);
        CallLog callLog = callDetailRecordMapper.toCallLog(patientDocumentId, kookooCallDetailRecord);
        callLog.maskAuthenticationPin();
        if (patientDocumentId != null)
            callLog.clinicId(allPatients.get(patientDocumentId).getClinic_id());
        allCallLogs.add(callLog);
    }

    public List<CallLog> getAll() {
        return allCallLogs.getAll();
    }

    public List<CallLog> getByClinicId(DateTime fromDate, DateTime toDate, String clinicId) {
        return allCallLogs.findByClinic(fromDate, toDate, clinicId);
    }

    public List<CallLog> getLogsBetweenDates(DateTime fromDate, DateTime toDate) {
        return allCallLogs.findCallLogsBetweenGivenDates(fromDate, toDate);
    }
}