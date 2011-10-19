package org.motechproject.tama.ivr.logging.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.CallEventCustomData;
import org.motechproject.tama.domain.CouchEntity;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.util.FileUtil;
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
        for (CallEvent callEvent : callEvents) {
            List<String> responses = callEvent.getData().getAll(CallEventConstants.CUSTOM_DATA_LIST);
            for (String response : responses) {
                if (StringUtils.isNotEmpty(response) && !response.contains(FileUtil.sanitizeFilename(TamaIVRMessage.SIGNATURE_MUSIC)))
                    return CALL_TYPE_AUTHENTICATED;
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

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
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
