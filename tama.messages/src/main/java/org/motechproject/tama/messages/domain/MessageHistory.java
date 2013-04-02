package org.motechproject.tama.messages.domain;

import lombok.Data;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'MessageHistory'")
@Data
public class MessageHistory extends CouchEntity {

    private String messageId;
    private DateTime lastPlayedOn;
    private int count;

    public MessageHistory() {
    }

    public MessageHistory(String messageId) {
        this.messageId = messageId;
    }

    public void setLastPlayedOn(DateTime lastPlayedOn) {
        this.lastPlayedOn = DateUtil.setTimeZone(lastPlayedOn);
    }

    public void readOn(DateTime dateTime) {
        lastPlayedOn = dateTime;
        count++;
    }

    public boolean neverPlayed() {
        return null == lastPlayedOn;
    }
}
