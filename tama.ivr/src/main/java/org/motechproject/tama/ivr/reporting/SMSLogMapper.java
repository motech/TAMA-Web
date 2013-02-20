package org.motechproject.tama.ivr.reporting;

import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.reports.contract.SMSLogRequest;

public class SMSLogMapper {

    private String externalId;
    private SMSType SMSType;
    private SMSLog smsLog;

    public SMSLogMapper(String externalId, SMSType SMSType, SMSLog smsLog) {
        this.externalId = externalId;
        this.SMSType = SMSType;
        this.smsLog = smsLog;
    }

    public SMSLogRequest map() {
        SMSLogRequest request = new SMSLogRequest();
        request.setContent(smsLog.getMessage());
        request.setExternalId(externalId);
        request.setSmsType(SMSType.getCode());
        request.setRecipientNumber(smsLog.getRecipient());
        request.setTimeStamp(smsLog.getSentDateTime().toDate());
        return request;
    }
}
