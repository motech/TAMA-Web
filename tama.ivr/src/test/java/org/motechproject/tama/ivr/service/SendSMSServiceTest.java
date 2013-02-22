package org.motechproject.tama.ivr.service;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.ivr.dto.SendSMSRequest;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.ivr.repository.AllSMSLogs;
import org.motechproject.tama.reporting.service.SMSReportingService;
import org.motechproject.tama.reports.contract.SMSLogRequest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSMSServiceTest {

    /*Dependencies*/
    @Mock
    private SmsService smsService;

    @Mock
    private AllSMSLogs allSMSLogs;

    @Mock
    private SMSReportingService smsReportingService;

    /*Test Data*/
    List<SendSMSRequest> recipients = Arrays.asList(
            new SendSMSRequest("recipient1", UUID.randomUUID().toString()),
            new SendSMSRequest("recipient2", UUID.randomUUID().toString())
    );

    String message = "message";

    private SendSMSService sendSMSService;


    public SendSMSServiceTest() {
        initMocks(this);
        sendSMSService = new SendSMSService(smsService, allSMSLogs, smsReportingService);
    }

    @Test
    public void shouldSendSMSToAllRecipients() {
        sendSMSService.send(recipients, message, SMSType.Clinician);
        ArgumentCaptor<String> recipient = ArgumentCaptor.forClass(String.class);
        verify(smsService, times(2)).sendSMS(recipient.capture(), eq(message));
        assertEquals(asList(recipients.get(0).getRecipientNumber(), recipients.get(1).getRecipientNumber()), recipient.getAllValues());
    }

    @Test
    public void shouldReportSMSLogs() {
        sendSMSService.send(recipients, message, SMSType.Clinician);
        verify(smsReportingService, times(2)).save(any(SMSLogRequest.class));
    }

    @Test
    public void shouldLogAfterSendingSMS() {
        sendSMSService.send(recipients.get(0), message, SMSType.OTC);
        verify(allSMSLogs).log(new SMSLog(recipients.get(0).getRecipientNumber(), message));
    }

    @Test
    public void shouldLogEverySMSSent() {
        sendSMSService.send(recipients, message, SMSType.Clinician);
        ArgumentCaptor<SMSLog> recipient = ArgumentCaptor.forClass(SMSLog.class);
        verify(allSMSLogs, times(2)).log(recipient.capture());
        assertEquals(
                asList(recipients.get(0).getRecipientNumber(), recipients.get(1).getRecipientNumber()),
                asList(recipient.getAllValues().get(0).getRecipient(), recipient.getAllValues().get(1).getRecipient())
        );
    }
}
