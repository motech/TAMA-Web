package org.motechproject.tama.messages.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.motechproject.tama.messages.domain.MessageHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class AllMessageHistories extends CouchDbRepositorySupport<MessageHistory> {

    @Autowired
    protected AllMessageHistories(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(MessageHistory.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public MessageHistory findByMessageId(String messageId) {
        List<MessageHistory> histories = queryView("by_messageId", messageId);
        return (!isEmpty(histories)) ? histories.get(0) : new MessageHistory(messageId);
    }

    public void upsert(MessageHistory messageHistory) {
        if (StringUtils.isNotBlank(messageHistory.getId())) {
            update(messageHistory);
        } else {
            add(messageHistory);
        }
    }
}
