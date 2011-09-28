package org.motechproject.tama.ivr.logging.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.mapper.CallLogMapper;
import org.motechproject.tama.ivr.logging.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallLogService {

    private final AllCallLogs allCallLogs;

    private final KookooCallDetailRecordsService kookooCallDetailRecordsService;

    private final CallLogMapper callDetailRecordMapper;

    @Autowired
    public CallLogService(AllCallLogs allCallDetails, KookooCallDetailRecordsService kookooCallDetailRecordsService,
                          CallLogMapper callDetailRecordMapper) {
        this.allCallLogs = allCallDetails;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.callDetailRecordMapper = callDetailRecordMapper;
    }

    public void log(String callId, String patientDocumentId) {
        KookooCallDetailRecord kookooCallDetailRecord = kookooCallDetailRecordsService.findByCallId(callId);
        if (kookooCallDetailRecord.getCallDetailRecord().getCallDirection() == IVRRequest.CallDirection.Inbound) {
            allCallLogs.add(callDetailRecordMapper.toCallLog(patientDocumentId, kookooCallDetailRecord));
        } else {
            CallLog openCallLog = allCallLogs.getLatestOpenCallLog(patientDocumentId);
            CallLog newCallLog = callDetailRecordMapper.toCallLog(patientDocumentId, kookooCallDetailRecord);
            openCallLog.copy(newCallLog);
            allCallLogs.update(openCallLog);
        }
    }

    public void logStartOfOutboundCall(String patientDocumentId) {
        allCallLogs.add(new CallLog(patientDocumentId));
    }
}