package org.motechproject.tamacallflow.mapper;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.tamadomain.domain.CallLog;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class CallLogMapper {
    public CallLog toCallLog(String patientDocumentId, KookooCallDetailRecord kookooCallDetailRecord) {
        CallLog callLog = new CallLog();
        CallDetailRecord callDetailRecord = kookooCallDetailRecord.getCallDetailRecord();

        callLog.setPatientDocumentId(patientDocumentId);
        callLog.setCallId(kookooCallDetailRecord.getVendorCallId());
        callLog.setPhoneNumber(callDetailRecord.getPhoneNumber());
        callLog.setCallEvents(callDetailRecord.getCallEvents());
        callLog.setStartTime(DateUtil.newDateTime(callDetailRecord.getStartDate()));
        callLog.setEndTime(DateUtil.newDateTime(callDetailRecord.getEndDate()));
        callLog.setCallDirection(callDetailRecord.getCallDirection());
        return callLog;
    }
}
