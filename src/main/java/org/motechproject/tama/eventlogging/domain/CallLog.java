package org.motechproject.tama.eventlogging.domain;

import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.tama.domain.CouchEntity;

public class CallLog extends CouchEntity {

    public static final String CALL_TYPE_SYMPTOM_REPORTING = "Symptom Reporting";

    public static final String CALL_TYPE_PILL_REMINDER = "Pill Reminder";

    private String callType;

    private String patientDocumentId;

    private CallDetailRecord callDetailRecord;

    public CallLog() {
    }

    public CallLog(String callType, String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
        this.callType = callType;
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
}
