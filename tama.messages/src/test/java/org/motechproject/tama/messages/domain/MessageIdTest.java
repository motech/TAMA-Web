package org.motechproject.tama.messages.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MessageIdTest {

    @Test
    public void shouldConcatenateTypeAndIdAsMessageId() {
        MessageId messageId = new MessageId(Method.PULL, "MessageType", "MessageId");
        assertEquals("PULLMessageTypeMessageId", messageId.getMessageId());
    }
}
