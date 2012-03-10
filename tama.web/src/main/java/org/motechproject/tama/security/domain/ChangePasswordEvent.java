package org.motechproject.tama.security.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ChangePasswordEvent extends TAMAEvent {

    @JsonProperty @Getter String clinicianName;
    @JsonProperty @Getter String clinicName;
    @JsonProperty @Getter String clinicId;
    @JsonProperty @Getter String username;
    @JsonProperty @Getter DateTime dateTime;

    public ChangePasswordEvent() {}

    public ChangePasswordEvent(String clinicianName, String clinicName, String clinicId, String username) {
        super(DateUtil.now());
        this.clinicianName = clinicianName;
        this.clinicName = clinicName;
        this.clinicId = clinicId;
        this.username = username;
    }
}