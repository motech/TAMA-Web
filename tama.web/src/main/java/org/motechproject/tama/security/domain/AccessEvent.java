package org.motechproject.tama.security.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.util.DateUtil;


@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class AccessEvent extends TAMAEvent {

    public enum AccessEventType {Login, Logout};

    @JsonProperty @Getter String userName;
    @JsonProperty @Getter String sourceAddress;
    @JsonProperty @Getter String sessionId;
    @JsonProperty @Getter AccessEventType eventType;
    @JsonProperty @Getter String loginStatus;

    public AccessEvent() {}

    public AccessEvent(String userName, String sourceAddress, String sessionId, AccessEventType eventType, String status) {
        super(DateUtil.now());
        this.userName = userName;
        this.sourceAddress = sourceAddress;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.loginStatus = status;
    }
}