package org.motechproject.tama.migration.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.ivr.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationMigration0_6To0_7Context.xml")
public class PagedCallLogsRepositoryIT extends SpringIntegrationTest {

    @Autowired
    private PagedCallLogsRepository allCallLogs;

    @Test
    public void shouldRetrieveCallLogsInAPagedManner() {
        List<CallLog> callLogs = asList(
                new CallLog("patientDocumentId1"),
                new CallLog("patientDocumentId2")
        );
        allCallLogs.addAll(callLogs);
        markForDeletion(callLogs);

        assertEquals("patientDocumentId1", allCallLogs.get(0, 1).get(0).getPatientDocumentId());
        assertEquals("patientDocumentId2", allCallLogs.get(1, 1).get(0).getPatientDocumentId());
    }
}
