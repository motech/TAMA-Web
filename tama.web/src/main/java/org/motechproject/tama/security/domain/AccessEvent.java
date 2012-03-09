package org.motechproject.tama.security.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'AccessEvent'")
public class AccessEvent extends CouchEntity {


    public enum AccessEventType {Login, Logout};

    @JsonProperty @Getter String userName;
    @JsonProperty @Getter String sourceAddress;
    @JsonProperty @Getter String sessionId;
    @JsonProperty @Getter DateTime dateTime;
    @JsonProperty @Getter AccessEventType eventType;
    @JsonProperty @Getter String loginStatus;

    public AccessEvent() {}

    public AccessEvent(String userName, String sourceAddress, String sessionId, AccessEventType eventType, String status) {
        this.userName = userName;
        this.sourceAddress = sourceAddress;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.loginStatus = status;
        this.dateTime = DateUtil.now();
    }
}