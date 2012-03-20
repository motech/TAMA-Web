package org.motechproject.tama.ivr.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

@TypeDiscriminator("doc.documentType == 'SMSLog'")
public class SMSLog extends CouchEntity {

    @Getter
    @JsonProperty
    private String recipient;
    @Getter
    @JsonProperty
    private String message;

    public SMSLog() {
    }

    public SMSLog(String recipient, String message) {
        this.message = message;
        this.recipient = recipient;
    }
}
