package org.motechproject.tama.common.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

@TypeDiscriminator("doc.documentType == 'AuditRecord'")
public class AuditRecord extends CouchEntity {

    @JsonProperty
    DateTime updatedTime;
    @JsonProperty
    String userName;

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    @JsonProperty CouchEntity before;

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    @JsonProperty CouchEntity after;

    public AuditRecord() {
    }

    public AuditRecord(DateTime updatedTime, String userName, CouchEntity before, CouchEntity after) {
        this.updatedTime = updatedTime;
        this.userName = userName;
        this.before = before;
        this.after = after;
    }

    @JsonIgnore
    public CouchEntity getBefore() {
        return before;
    }
}
