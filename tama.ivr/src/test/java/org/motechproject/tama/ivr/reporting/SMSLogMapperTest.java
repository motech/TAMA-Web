package org.motechproject.tama.ivr.reporting;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.reports.contract.SMSLogRequest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class SMSLogMapperTest {

    private SMSLogRequest request;
    private SMSLog log;

    @Before
    public void setup() {
        request = new SMSLogRequest();
        log = new SMSLog("1234567890", "message");
        log.setSentDateTime(DateUtil.now());
    }

    @Test
    public void shouldMapExternalId() {
        assertNull(request.getExternalId());
        request = new SMSLogMapper("externalId", SMSType.OTC, log).map();
        assertEquals("externalId", request.getExternalId());
    }

    @Test
    public void shouldMapContent() {
        assertNull(request.getContent());
        request = new SMSLogMapper("externalId", SMSType.OTC, log).map();
        assertEquals(log.getMessage(), request.getContent());
    }

    @Test
    public void shouldMapSMSType() {
        assertNull(request.getSmsType());
        request = new SMSLogMapper("externalId", SMSType.OTC, log).map();
        assertEquals("o", request.getSmsType());
    }

    @Test
    public void shouldMapRecipientNumber() {
        assertNull(request.getRecipientNumber());
        request = new SMSLogMapper("externalId", SMSType.OTC, log).map();
        assertEquals(log.getRecipient(), request.getRecipientNumber());
    }

    @Test
    public void shouldMapTimeStamp() {
        assertNull(request.getRecipientNumber());
        request = new SMSLogMapper("externalId", SMSType.OTC, log).map();
        assertEquals(log.getSentDateTime().toDate(), request.getTimeStamp());
    }
}
