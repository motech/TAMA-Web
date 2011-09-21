package org.motechproject.tama.eventlogging.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.eventlogging.domain.CallDetail;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring/applicationContext.xml"})
public class AllCallDetailsTest extends SpringIntegrationTest{

    @Autowired
    private AllCallDetails allCallDetails;

    @Test
    public void shouldGetCallLogByCallId() {
        CallDetail callDetail = new CallDetail();
        String callId = "callId";
        callDetail.setCallId(callId);

        allCallDetails.add(callDetail);

        CallDetail byCallId = allCallDetails.getByCallId(callId);
        assertNotNull(byCallId);
        assertEquals(callDetail.getCallId(), byCallId.getCallId());
        markForDeletion(callDetail);
    }
}
