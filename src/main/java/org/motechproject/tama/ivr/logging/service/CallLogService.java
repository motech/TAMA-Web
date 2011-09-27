package org.motechproject.tama.ivr.logging.service;

import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
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

    public void create(EndOfCallEvent callEvent) {
        KookooCallDetailRecord callDetailRecord = kookooCallDetailRecordsService.findByCallId(callEvent.getCallId());
        allCallLogs.add(callDetailRecordMapper.toCallLog(callDetailRecord));
    }
}