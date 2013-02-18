package org.motechproject.tama.web.view;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.log.CallFlowGroupView;
import org.motechproject.tama.ivr.log.CallFlowGroupViews;

import java.util.List;

public class CallLogView {

    private String patientId;
    private CallLog callLog;
    private String clinicName;
    private List<String> likelyPatientIds;
    private String callDate;
    private LocalTime callStartTime;
    private LocalTime callEndTime;
    private CallFlowGroupViews flowGroupViews;

    public CallLogView(String patientId, CallLog callLog, String clinicName, List<String> likelyPatientIds) {
        this.patientId = patientId;
        this.callLog = callLog;
        this.clinicName = clinicName;
        this.likelyPatientIds = likelyPatientIds;
        flowGroupViews = new CallFlowGroupViews(callLog);
        setCallDate();
        setCallStartTime();
        setCallEndTime();
    }

    public LocalTime getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime() {
        callStartTime = callLog.getCallEvents().isEmpty() ? callLog.getStartTime().toLocalTime() : callLog.getCallEvents().get(0).getTimeStamp().toLocalTime();
    }

    public LocalTime getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime() {
        callEndTime = callLog.getEndTime().toLocalTime();
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd YYYY");
        callDate = callLog.getCallEvents().isEmpty() ? formatter.print(callLog.getStartTime()) : formatter.print(callLog.getCallEvents().get(0).getTimeStamp());
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

    public List<String> getLikelyPatientIds() {
        return likelyPatientIds;
    }

    public List<CallFlowGroupView> getCallFlowGroupViews() {
        return flowGroupViews.getCallFlowGroupViews();
    }

    public String getFlows() {
        return flowGroupViews.getFlows();
    }

    public String getTitle() {
        String patientInfo = StringUtils.isEmpty(patientId) ? StringUtils.join(likelyPatientIds, " or ") : patientId;
        if (callLog.getCallDirection() == CallDirection.Outbound) {
            return "Tama called " + patientInfo + " || Clinic :" + clinicName;
        } else {
            return patientInfo + " called Tama" + " || Clinic :" + clinicName;
        }
    }

    public CallFlowGroupViews getFlowGroupViews() {
        return flowGroupViews;
    }

    public boolean isMissedCall() {
        return flowGroupViews.hasMissedEvent();
    }
}

