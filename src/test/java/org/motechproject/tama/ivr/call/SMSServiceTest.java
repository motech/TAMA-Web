package org.motechproject.tama.ivr.call;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpClient;

public class SMSServiceTest extends TestCase {

    SMSService smsService;

    public void testSendSMS() throws Exception {
        smsService = new SMSService(new HttpClient());
        smsService.sendSMS("9632231618", "HeeeHeee!");
    }
}
