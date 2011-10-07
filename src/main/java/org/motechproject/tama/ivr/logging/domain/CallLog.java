package org.motechproject.tama.ivr.logging.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class CallLog extends CouchEntity {

    public static final String CALL_TYPE_SYMPTOM_REPORTING = "Symptom Reporting";
    public static final String CALL_TYPE_PILL_REMINDER = "Pill Reminder";
    public static final String CALL_TYPE_UNAUTHENTICATED = "Unauthenticated";


    private String patientDocumentId;

    private DateTime startTime;
    private DateTime endTime;
    private String phoneNumber;

    private String callId;
    private IVRRequest.CallDirection callDirection;
    private List<CallEvent> callEvents = new ArrayList<CallEvent>();

    private String clinicId;

    public CallLog() {
    }

    public CallLog(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

    @JsonIgnore
    public String getCallType() {
        for(CallEvent callEvent : callEvents){
            if(callEvent.getData().get(CallEventConstants.AUTHENTICATION_EVENT)!=null){
                return callEvent.getData().get(CallEventConstants.CALL_TYPE);
            }
        }
        return CALL_TYPE_UNAUTHENTICATED;
    }

    public DateTime getStartTime() {
        return DateUtil.setTimeZone(startTime);
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return DateUtil.setTimeZone(endTime);
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
        return this.callEvents;
    }

    public void setCallEvents(List<CallEvent> callEvents) {
        this.callEvents = callEvents;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }
}
