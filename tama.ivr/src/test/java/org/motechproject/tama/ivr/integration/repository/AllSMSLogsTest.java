package org.motechproject.tama.ivr.integration.repository;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.ivr.repository.AllSMSLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath*:applicationIVRContext.xml", inheritLocations = false)
public class AllSMSLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllSMSLogs allSMSLogs;

    @Test
    public void shouldPersistLog() {
        allSMSLogs.log("recipient", "message");

        List<SMSLog> logs = allSMSLogs.getAll();
        assertTrue(CollectionUtils.isNotEmpty(logs));
        markForDeletion(logs);
    }
}