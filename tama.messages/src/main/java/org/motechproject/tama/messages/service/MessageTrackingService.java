package org.motechproject.tama.messages.service;


import org.motechproject.tama.messages.domain.MessageHistory;
import org.motechproject.tama.messages.domain.MessageId;
import org.motechproject.tama.messages.domain.Method;
import org.motechproject.tama.messages.repository.AllMessageHistories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.now;

@Component
public class MessageTrackingService {

    private AllMessageHistories allMessageHistories;

    @Autowired
    public MessageTrackingService(AllMessageHistories allMessageHistories) {
        this.allMessageHistories = allMessageHistories;
    }

    public void markAsRead(Method method, String messageType, String id) {
        MessageHistory history = allMessageHistories.findByMessageId(new MessageId(method, messageType, id).getMessageId());
        history.readOn(now());
        allMessageHistories.upsert(history);
    }

    public MessageHistory get(Method method, String messageType, String id) {
        return allMessageHistories.findByMessageId(new MessageId(method, messageType, id).getMessageId());
    }
}
