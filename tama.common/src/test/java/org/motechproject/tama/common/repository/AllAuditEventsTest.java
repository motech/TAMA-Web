package org.motechproject.tama.common.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.domain.AuditEvent;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@ContextConfiguration(locations = "classpath*:applicationCommonContext.xml", inheritLocations = false)
public class AllAuditEventsTest extends SpringIntegrationTest {

    @Autowired
    AllAuditEvents allAuditEvents;

    @Test
    public void shouldRecordAppointmentEvent() {
        String id = allAuditEvents.recordAppointmentEvent("user", "description");
        AuditEvent savedAuditEvent = allAuditEvents.get(id);
        assertNotNull(savedAuditEvent);
        assertEquals(AuditEvent.AuditEventType.Appointment, savedAuditEvent.getEventType());
    }

    @Test
    public void shouldRecordAlertEvent() {
        String id = allAuditEvents.recordAlertEvent("user", "description");
        AuditEvent savedAuditEvent = allAuditEvents.get(id);
        assertNotNull(savedAuditEvent);
        assertEquals(AuditEvent.AuditEventType.Alert, savedAuditEvent.getEventType());
    }

    @After
    public void tearDown() {
        allAuditEvents.removeAll();
    }

}
