package org.motechproject.tama.security.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.util.DateUtil;


@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class AccessEvent extends TAMAEvent {

    public enum AccessEventType {Login, Logout};

    @JsonProperty String userName;
    @JsonProperty String sourceAddress;
    @JsonProperty String sessionId;
    @JsonProperty AccessEventType eventType;
    @JsonProperty String loginStatus;

    public AccessEvent() {}

    public AccessEvent(String userName, String sourceAddress, String sessionId, AccessEventType eventType, String status) {
        super(DateUtil.now());
        this.userName = userName;
        this.sourceAddress = sourceAddress;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.loginStatus = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public AccessEventType getEventType() {
        return eventType;
    }

    public void setEventType(AccessEventType eventType) {
        this.eventType = eventType;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }
}