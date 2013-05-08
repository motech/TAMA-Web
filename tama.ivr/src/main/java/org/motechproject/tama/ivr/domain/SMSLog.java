package org.motechproject.tama.ivr.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'SMSLog'")
public class SMSLog extends CouchEntity {

    @Getter
    @JsonProperty
    private String recipient;
    @Getter
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

    @JsonIgnore
    public String getMaskedMessage() {
        return message.replaceAll("[0-9]{10}", "XXXXXXXXXX");
    }
}
