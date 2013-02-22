package org.motechproject.tama.ivr.service;

import org.apache.log4j.Logger;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.ivr.dto.SendSMSRequest;
import org.motechproject.tama.ivr.reporting.SMSLogMapper;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.ivr.repository.AllSMSLogs;
import org.motechproject.tama.reporting.service.SMSReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendSMSService {

    Logger logger = Logger.getLogger(SendSMSService.class);

    private SmsService smsService;
    private AllSMSLogs allSMSLogs;
    private SMSReportingService smsReportingService;

    @Autowired
    public SendSMSService(SmsService smsService, AllSMSLogs allSMSLogs, SMSReportingService smsReportingService) {
        this.smsService = smsService;
        this.allSMSLogs = allSMSLogs;
        this.smsReportingService = smsReportingService;
    }

    public void send(SendSMSRequest recipient, String message, SMSType smsType) {
        SMSLog log = new SMSLog(recipient.getRecipientNumber(), message);
        logger.debug("Sending SMS:recipient-" + recipient + ":message-" + message);

        smsService.sendSMS(recipient.getRecipientNumber(), message);
        allSMSLogs.log(log);
        smsReportingService.save(new SMSLogMapper(recipient.getExternalId(), smsType, log).map());
    }

    public void send(List<SendSMSRequest> recipients, String message, SMSType smsType) {
        // Cannot use platform's send(List<String>, String) because of the interface kookoo supports
        for (SendSMSRequest recipient : recipients) {
            send(recipient, message, smsType);
        }
    }
}