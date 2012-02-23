package org.motechproject.tama.ivr.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'CallLog'")
public class CallLog extends CouchEntity {
    public static final String CALL_TYPE_UNAUTHENTICATED = "Unauthenticated";
    public static final String CALL_TYPE_AUTHENTICATED = "Authenticated";
    private String patientDocumentId;
    private DateTime startTime;
    private DateTime endTime;
    private String phoneNumber;

    private String callId;
    private CallDirection callDirection;
    private List<String> likelyPatientIds = new ArrayList<String>();
    private List<CallEvent> callEvents = new ArrayList<CallEvent>();

    @JsonProperty
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
        return this.patientDocumentId == null ? CALL_TYPE_UNAUTHENTICATED : CALL_TYPE_AUTHENTICATED;
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

    public CallDirection getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public List<CallEvent> getCallEvents() {
        return this.callEvents;
    }

    public void setCallEvents(List<CallEvent> callEvents) {
        this.callEvents = callEvents;
    }

    public String clinicId() {
        return clinicId;
    }

    public CallLog clinicId(String clinicId) {
        this.clinicId = clinicId;
        return this;
    }

    public List<String> getLikelyPatientIds() {
        return likelyPatientIds;
    }

    public void setLikelyPatientIds(List<String> likelyPatientIds) {
        this.likelyPatientIds = likelyPatientIds;
    }

    public void maskAuthenticationPin() {
        for (CallEvent callEvent : callEvents) {
            CallEventCustomData customData = callEvent.getData();
            String dtmfData = customData.getFirst(CallEventConstants.DTMF_DATA);
            if (StringUtils.isNotEmpty(dtmfData) && dtmfData.length() > 2) {
                customData.update(CallEventConstants.DTMF_DATA, "****");
            }
        }
    }
}
