package org.motechproject.tama.ivr.service;

import org.apache.log4j.Logger;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.tama.ivr.dto.SendSMSRequest;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.ivr.repository.AllSMSLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendSMSService {

    Logger logger = Logger.getLogger(SendSMSService.class);

    private SmsService smsService;
    private AllSMSLogs allSMSLogs;

    @Autowired
    public SendSMSService(SmsService smsService, AllSMSLogs allSMSLogs) {
        this.smsService = smsService;
        this.allSMSLogs = allSMSLogs;
    }

    public void send(SendSMSRequest recipient, String message, SMSType smsType) {
        logger.debug("Sending SMS:recipient-" + recipient + ":message-" + message);
        smsService.sendSMS(recipient.getRecipientNumber(), message);
        allSMSLogs.log(recipient.getRecipientNumber(), message);
    }

    public void send(List<SendSMSRequest> recipients, String message, SMSType smsType) {
        // Cannot use platform's send(List<String>, String) because of the interface kookoo supports
        for (SendSMSRequest recipient : recipients) {
            send(recipient, message, smsType);
        }
    }
}