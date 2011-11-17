package org.motechproject.tama.web.view;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.logging.domain.CallLog;

import java.util.ArrayList;
import java.util.List;

public class CallLogView {

    private String patientId;
    private CallLog callLog;
    private String clinicName;
    private String callDateFromCallLogDateTime;
    private LocalTime callStartTimeFromCallLogStartDateTime;
    private LocalTime callEndTimeFromCallLogEndDateTime;

    public CallLogView(String patientId, CallLog callLog, String clinicName) {
        this.patientId = patientId;
        this.callLog = callLog;
        this.clinicName = clinicName;
        setCallDateFromCallLogDateTime();
        setCallStartTimeFromCallLogDateTime();
        setCallEndTimeFromCallLogDateTime();
    }

    public LocalTime getCallStartTimeFromCallLogStartDateTime() {
        return callStartTimeFromCallLogStartDateTime;
    }

    public void setCallStartTimeFromCallLogDateTime() {
        callStartTimeFromCallLogStartDateTime = callLog.getStartTime().toLocalTime();
    }

    public LocalTime getCallEndTimeFromCallLogEndDateTime() {
        return callEndTimeFromCallLogEndDateTime;
    }

    public void setCallEndTimeFromCallLogDateTime() {
        callEndTimeFromCallLogEndDateTime = callLog.getEndTime().toLocalTime();
    }

    public String getCallDateFromCallLogDateTime() {
        return callDateFromCallLogDateTime;
    }

    public void setCallDateFromCallLogDateTime(){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd YYYY");
        callDateFromCallLogDateTime = formatter.print(callLog.getStartTime());
    }

    public String getPatientId() {
        return patientId;
    }

    public CallLog getCallLog() {
        return callLog;
    }

    public String getClinicName() {
        return clinicName;
    }

    public List<CallEventView> getCallEvents() {
        List<CallEventView> callEventViews = new ArrayList<CallEventView>();
        for (CallEvent callEvent : callLog.getCallEvents()) {
            callEventViews.add(new CallEventView(callEvent));
        }
        return callEventViews;
    }

    public String getTitle() {
        if (callLog.getCallDirection() == CallDirection.Outbound) {
            return "Tama called " + patientId + " || Clinic :" + clinicName;
        } else {
            return patientId + " called Tama" + " || Clinic :" + clinicName;
        }

    }
}

