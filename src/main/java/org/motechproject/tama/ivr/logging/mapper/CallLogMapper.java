package org.motechproject.tama.ivr.logging.mapper;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class CallLogMapper {

    public CallLog toCallLog(String patientDocumentId, KookooCallDetailRecord kookooCallDetailRecord) {
        CallLog callLog = new CallLog();
        CallDetailRecord callDetailRecord = kookooCallDetailRecord.getCallDetailRecord();

        callLog.setPatientDocumentId(patientDocumentId);
        callLog.setCallId(kookooCallDetailRecord.getCallId());
        callLog.setPhoneNumber(callDetailRecord.getPhoneNumber());
        callLog.setCallEvents(callDetailRecord.getCallEvents());
        callLog.setStartTime(DateUtil.newDateTime(callDetailRecord.getStartDate()));
        callLog.setEndTime(DateUtil.newDateTime(callDetailRecord.getEndDate()));
        callLog.setCallDirection(callDetailRecord.getCallDirection());
        return callLog;
    }
}
