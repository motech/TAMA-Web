package org.motechproject.tama.messages.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.messages.domain.MessageHistory;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationMessagesContext.xml", inheritLocations = false)
public class AllMessageHistoriesIT extends SpringIntegrationTest {

    @Autowired
    AllMessageHistories allMessageHistories;

    @Test
    public void shouldFindMessageById() {
        String messageId = "messageId";

        MessageHistory history = new MessageHistory(messageId);
        allMessageHistories.add(history);
        markForDeletion(history);

        assertEquals(messageId, allMessageHistories.findByMessageId(messageId).getMessageId());
    }

    @Test
    public void shouldCreateNewHistoryIfHistoryDoesNotExist() {
        String messageId = "messageId";

        MessageHistory history = new MessageHistory(messageId);
        allMessageHistories.upsert(history);
        markForDeletion(history);

        assertEquals(messageId, allMessageHistories.findByMessageId(messageId).getMessageId());
    }

    @Test
    public void shouldUpdateExistingHistoryIfHistoryExists() {
        String messageId = "messageId";
        DateTime now = DateUtil.now();

        MessageHistory history = new MessageHistory(messageId);
        allMessageHistories.upsert(history);
        markForDeletion(history);


        history.readOn(now);
        allMessageHistories.upsert(history);
        markForDeletion(history);

        assertEquals(now, allMessageHistories.findByMessageId(messageId).getLastPlayedOn());
    }
}
