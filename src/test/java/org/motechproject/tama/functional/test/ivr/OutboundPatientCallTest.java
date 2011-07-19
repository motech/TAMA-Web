package org.motechproject.tama.functional.test.ivr;

import org.junit.Test;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.outbound.OutboundCallService;
import org.springframework.beans.factory.annotation.Autowired;

public class OutboundPatientCallTest extends SpringIntegrationTest {

    @Autowired
    private OutboundCallService outboundCallService;

    @Test
    public void shouldCallPatient(){
        outboundCallService.call("f7dba5f96baeacfb047e74cac903a291");
    }
}
