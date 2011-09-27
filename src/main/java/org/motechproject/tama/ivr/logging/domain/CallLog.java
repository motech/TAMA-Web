package org.motechproject.tama.ivr.logging.domain;

import org.joda.time.DateTime;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.CouchEntity;

import java.util.ArrayList;
import java.util.List;

public class CallLog extends CouchEntity {

    public static final String CALL_TYPE_SYMPTOM_REPORTING = "Symptom Reporting";
    public static final String CALL_TYPE_PILL_REMINDER = "Pill Reminder";

    private String patientDocumentId;
    private String callType;

    private DateTime startTime;
    private DateTime endTime;
    private String phoneNumber;

    private String callId;
    private IVRRequest.CallDirection callDirection;
    private List<CallEvent> callEvents = new ArrayList<CallEvent>();

    public CallLog() {
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public IVRRequest.CallDirection getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(IVRRequest.CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public List<CallEvent> getCallEvents() {
        return callEvents;
    }

    public void setCallEvents(List<CallEvent> callEvents) {
        this.callEvents = callEvents;
    }
}
