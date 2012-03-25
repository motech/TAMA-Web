package org.motechproject.tama.ivr.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'SMSLog'")
public class SMSLog extends CouchEntity {

    @JsonProperty
    private String recipient;
    @JsonProperty
    private String message;
    private DateTime sentDateTime;

    public SMSLog() {
    }

    public SMSLog(String recipient, String message) {
        this.message = message;
        this.recipient = recipient;
        this.sentDateTime = DateUtil.now();
    }

    public DateTime getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(DateTime sentDateTime) {
        this.sentDateTime = DateUtil.setTimeZone(sentDateTime);
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
