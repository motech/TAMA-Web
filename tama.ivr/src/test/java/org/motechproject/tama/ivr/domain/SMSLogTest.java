package org.motechproject.tama.ivr.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SMSLogTest {

    @Test
    public void shouldMaskPhoneNumberInMessageContent() {
        SMSLog smsLog = new SMSLog("1234567890", "1234567890");
        assertEquals("XXXXXXXXXX", smsLog.getMaskedMessage());
    }

    @Test
    public void shouldNotMaskPhoneNumberInEmptyMessageContent() {
        SMSLog smsLog = new SMSLog("1234567890", "");
        assertEquals("", smsLog.getMaskedMessage());
    }

    @Test
    public void shouldNotMaskOtherNumbersInMessageContent() {
        String notAPhoneNumber = "123456789";
        SMSLog smsLog = new SMSLog("1234567890", notAPhoneNumber);
        assertEquals(notAPhoneNumber, smsLog.getMaskedMessage());
    }
}
