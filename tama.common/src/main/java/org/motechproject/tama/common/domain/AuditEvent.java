package org.motechproject.tama.common.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

@TypeDiscriminator("doc.documentType == 'AuditEvent'")
public class AuditEvent extends CouchEntity {

    @JsonProperty
    DateTime dateTime;
    @JsonProperty
    String userName;
    @JsonProperty
    @Getter
    String description;
    @JsonProperty
    @Getter
    AuditEventType eventType;

    public AuditEvent() {
    }

    public AuditEvent(String userName, DateTime dateTime, AuditEventType eventType, String description) {
        this.userName = userName;
        this.dateTime = dateTime;
        this.eventType = eventType;
        this.description = description;
    }

    public enum AuditEventType {
        Appointment,
        Alert
    }
}
