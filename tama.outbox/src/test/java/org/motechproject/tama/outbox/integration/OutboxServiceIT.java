package org.motechproject.tama.outbox.integration;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.outbox.service.OutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath*:applicationOutboxContext.xml", inheritLocations = false)
public class OutboxServiceIT extends SpringIntegrationTest {

    @Autowired
    OutboxService outboxService;

    @Test
    public void shouldAssignMessageIdWhenAddingTheMessage() {
        String id = outboxService.addMessage("patientId", TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        assertTrue(StringUtils.isNotEmpty(id));
    }
}
