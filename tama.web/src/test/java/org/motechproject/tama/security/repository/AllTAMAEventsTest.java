package org.motechproject.tama.security.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.security.domain.AccessEvent;
import org.motechproject.tama.security.domain.ChangePasswordEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:META-INF/spring/applicationContext.xml", inheritLocations = false)
public class AllTAMAEventsTest extends SpringIntegrationTest{

    @Autowired
    AllTAMAEvents allTAMAEvents;

    @After
    public void tearDown() {
        super.after();
    }

    @Test
    public void shouldPersist_AndRetrieve_LoginEvent_AndChangePasswordEvent() throws Exception {
        String id = allTAMAEvents.newLoginEvent("test", "0.0.0.0", "sid", "Login");
        String changePasswordEventId = allTAMAEvents.newChangePasswordEvent("chg", "clinic", "clinicid", "username");
        markForDeletion(allTAMAEvents.get(id));
        markForDeletion(allTAMAEvents.get(changePasswordEventId));

        AccessEvent accessEvent = (AccessEvent) allTAMAEvents.get(id);

        assertNotNull(accessEvent);
        assertEquals("sid", accessEvent.getSessionId());

        ChangePasswordEvent changePasswordEvent = (ChangePasswordEvent) allTAMAEvents.get(changePasswordEventId);
        assertEquals("username", changePasswordEvent.getUsername());
    }
}