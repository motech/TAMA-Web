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
    String description;
    @JsonProperty
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

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuditEventType eventType) {
        this.eventType = eventType;
    }
}
