package org.motechproject.tama.security.domain;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.motechproject.util.DateUtil;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ChangePasswordEvent extends TAMAEvent {

    @JsonProperty String clinicianName;
    @JsonProperty String clinicName;
    @JsonProperty String clinicId;
    @JsonProperty String username;

    public ChangePasswordEvent() {}

    public ChangePasswordEvent(String clinicianName, String clinicName, String clinicId, String username) {
        super(DateUtil.now());
        this.clinicianName = clinicianName;
        this.clinicName = clinicName;
        this.clinicId = clinicId;
        this.username = username;
    }

    public String getClinicianName() {
        return clinicianName;
    }

    public void setClinicianName(String clinicianName) {
        this.clinicianName = clinicianName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}