package org.motechproject.tama.ivr.service;

import org.apache.log4j.Logger;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendSMSService {

    Logger logger = Logger.getLogger(SendSMSService.class);

    private SmsService smsService;

    @Autowired
    public SendSMSService(SmsService smsService) {
        this.smsService = smsService;
    }

    public void send(String recipient, String messageBody) {
        smsService.sendSMS(recipient, messageBody);
    }

    public void send(List<String> recipients, String messageBody) {
        for (String recipient : recipients) {
            logger.debug("Sending SMS:recipient-" + recipient + ":message-" + messageBody);
            smsService.sendSMS(recipient, messageBody);
        }
    }
}