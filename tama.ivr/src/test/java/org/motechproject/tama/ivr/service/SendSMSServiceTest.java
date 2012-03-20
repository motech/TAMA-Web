package org.motechproject.tama.ivr.service;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.tama.ivr.repository.AllSMSLogs;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
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

    /*Test Data*/
    List<String> recipients = Arrays.asList("recipient1", "recipient2");
    String message = "message";

    private SendSMSService sendSMSService;

    public SendSMSServiceTest() {
        initMocks(this);
        sendSMSService = new SendSMSService(smsService, allSMSLogs);
    }

    @Test
    public void shouldSendSMSToAllRecipients() {
        sendSMSService.send(recipients, message);
        ArgumentCaptor<String> recipient = ArgumentCaptor.forClass(String.class);
        verify(smsService, times(2)).sendSMS(recipient.capture(), eq(message));
        assertArrayEquals(recipients.toArray(), recipient.getAllValues().toArray());
    }

    @Test
    public void shouldLogAfterSendingSMS() {
        sendSMSService.send(recipients.get(0), message);
        verify(allSMSLogs).log(recipients.get(0), message);
    }

    @Test
    public void shouldLogEverySMSSent() {
        sendSMSService.send(recipients, message);
        ArgumentCaptor<String> recipient = ArgumentCaptor.forClass(String.class);
        verify(allSMSLogs, times(2)).log(recipient.capture(), eq(message));
        assertArrayEquals(recipients.toArray(), recipient.getAllValues().toArray());
    }
}
