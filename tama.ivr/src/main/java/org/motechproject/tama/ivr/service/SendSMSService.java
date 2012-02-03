package org.motechproject.tama.ivr.service;

import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendSMSService {

    private SmsService smsService;

    @Autowired
    public SendSMSService(SmsService smsService){
        this.smsService = smsService;
    }

    public void send(String recipient, String messageBody){
        smsService.sendSMS(recipient, messageBody);
    }

    public void send(List<String> recipients, String messageBody){
        for(String recipient : recipients){
            smsService.sendSMS(recipient, messageBody);
        }
    }
}