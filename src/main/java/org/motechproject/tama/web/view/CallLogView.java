package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.motechproject.server.service.ivr.IVRRequest;

import java.util.List;

public class CallLogView {

    private String callType;

    private String patientId;

    private DateTime startTime;

    private DateTime endTime;

    private String phoneNumber;

    private String callId;

    private IVRRequest.CallDirection callDirection;

    private List<CallEventView> callEvents;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public IVRRequest.CallDirection getCallDirection() {
        return callDirection;
    }

    public String getCallId() {
        return callId;
    }

    public String getCallType() {
        return callType;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setCallDirection(IVRRequest.CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public List<CallEventView> getCallEvents() {
        return callEvents;
    }

    public void setCallEvents(List<CallEventView> callEvents) {
        this.callEvents = callEvents;
    }

    public String getTitle(){
        if(callDirection == IVRRequest.CallDirection.Outbound){
            return "Tama called " + patientId;
        }
        else {
            return patientId + " called Tama";
        }

    }
}

