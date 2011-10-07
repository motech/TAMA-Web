package org.motechproject.tama.web.view;

import org.joda.time.LocalTime;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class CallLogView {

    private String patientId;
    private CallLog callLog;
    private String clinicName;
    private String callDateFromCallLogDateTime;
    private LocalTime callStartTimeFromCallLogStartDateTime;
    private LocalTime callEndTimeFromCallLogEndDateTime;
    public static final int STRING_BEGIN_INDEX = 0;
    public static final String RIGHT_BOUNDING_SUBSEQUENCE = "00:00:00";
    public final String LEFT_BOUNDING_SUBSEQUENCE = "IST";
    public final int EXCLUDING_FACTOR = 3;

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
        String dateToBeFormatted = DateUtil.newDate(callLog.getStartTime().toDate()).toDate().toString();
        callDateFromCallLogDateTime = dateToBeFormatted.substring(STRING_BEGIN_INDEX,dateToBeFormatted.indexOf(RIGHT_BOUNDING_SUBSEQUENCE)).trim()
                                    + dateToBeFormatted.substring(dateToBeFormatted.indexOf(LEFT_BOUNDING_SUBSEQUENCE)+ EXCLUDING_FACTOR, dateToBeFormatted.length());
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
        if (callLog.getCallDirection() == IVRRequest.CallDirection.Outbound) {
            return "Tama called " + patientId + " || Clinic :" + clinicName;
        } else {
            return patientId + " called Tama" + " || Clinic :" + clinicName;
        }

    }
}

