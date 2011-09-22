package org.motechproject.tama.eventlogging.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.motechproject.tama.domain.CouchEntity;

import java.util.ArrayList;
import java.util.List;

public class CallDetail extends CouchEntity{

    private String callId;

    private String callType;

    private String callDirection;

    private String patientDocumentId;

    private String patientPhoneNumber;

    private DateTime startTime;

    private DateTime endTime;

    private List<CallDetailUnit> callEvents = new ArrayList<CallDetailUnit>();

    public CallDetail() {
    }

    public CallDetail(String callId, String callType, String callDirection, String patientDocumentId, String patientPhoneNumber) {
        this.patientPhoneNumber = patientPhoneNumber;
        this.patientDocumentId = patientDocumentId;
        this.callDirection = callDirection;
        this.callType = callType;
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

    public String getPatientPhoneNumber() {
        return patientPhoneNumber;
    }

    public void setPatientPhoneNumber(String patientPhoneNumber) {
        this.patientPhoneNumber = patientPhoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(String callDirection) {
        this.callDirection = callDirection;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public List<CallDetailUnit> getCallEvents() {
        return callEvents;
    }

    public void setCallEvents(List<CallDetailUnit> callEvents) {
        this.callEvents = callEvents;
    }

    @JsonIgnore
    public void add(CallDetailUnit eventLog) {
        this.callEvents.add(eventLog);
    }
}
