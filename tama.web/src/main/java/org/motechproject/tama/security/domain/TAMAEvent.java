package org.motechproject.tama.security.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;

@TypeDiscriminator("doc.baseClass == 'TAMAEvent'")
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class TAMAEvent extends CouchEntity {

    @JsonProperty @Getter DateTime dateTime;
    @JsonProperty String baseClass = "TAMAEvent";

    public TAMAEvent() {
        type = TAMAEvent.class.getSimpleName();
    }

    public TAMAEvent(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}