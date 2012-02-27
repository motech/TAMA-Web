package org.motechproject.tama.web.view;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;

import java.util.*;

import static org.motechproject.tama.web.view.CallFlowConstants.TREE_TO_FLOW_MAP;

public class CallLogView {

    private String patientId;
    private CallLog callLog;
    private String clinicName;
    private List<String> likelyPatientIds;
    private String callDateFromCallLogDateTime;
    private LocalTime callStartTimeFromCallLogStartDateTime;
    private LocalTime callEndTimeFromCallLogEndDateTime;
    private List<CallFlowGroupView> callFlowGroupViews;
    private String flows;
    private boolean authenticated;
    private boolean missed;

    public CallLogView(String patientId, CallLog callLog, String clinicName, List<String> likelyPatientIds) {
        this.patientId = patientId;
        this.callLog = callLog;
        this.clinicName = clinicName;
        this.likelyPatientIds = likelyPatientIds;
        callFlowGroupViews = new ArrayList<CallFlowGroupView>();
        setCallDateFromCallLogDateTime();
        setCallStartTimeFromCallLogDateTime();
        setCallEndTimeFromCallLogDateTime();
        setCallFlowGroupViews();
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

    public void setCallDateFromCallLogDateTime() {
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

    public List<String> getLikelyPatientIds() {
        return likelyPatientIds;
    }

    public List<CallFlowGroupView> getCallFlowGroupViews() {
        return callFlowGroupViews;
    }

    public String getFlows(){
        return flows;
    }

    public String getTitle() {
        String patientInfo = StringUtils.isEmpty(patientId) ? StringUtils.join(likelyPatientIds, " or ") : patientId;
        if (callLog.getCallDirection() == CallDirection.Outbound) {
            return "Tama called " + patientInfo + " || Clinic :" + clinicName;
        } else {
            return patientInfo + " called Tama" + " || Clinic :" + clinicName;
        }
    }

    public void setCallFlowGroupViews(){
        createCallFlowGroupViews();
        constructCallFlowTitle();
    }

    private void createCallFlowGroupViews() {
        for (CallEvent callEvent : callLog.getCallEvents()) {
            CallEventView callEventView = new CallEventView(callEvent);
            String flowToWhichCallEventBelongs = getFlow(callEventView);
            if (flowToWhichCallEventBelongs.equals(getFlowOfLastCallFlowGroup())) {
                addEventToLastFlow(callEventView);
            } else {
                createNewFlow(flowToWhichCallEventBelongs, callEventView);
            }
        }
    }

    private void constructCallFlowTitle() {
        Set<String> flowsInViews = new HashSet<String>();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            String callFlowGroupViewFlow = callFlowGroupView.getFlow();
            flowsInViews.add(callFlowGroupViewFlow);
        }
        if(authenticated) {
            flows = StringUtils.join(flowsInViews, ", ");
        } else if(missed) {
            flows = CallTypeConstants.MISSED;
        } else{
            flows = CallTypeConstants.UNAUTHENTICATED;
        }
    }

    private void createNewFlow(String flow, CallEventView callEventView) {
        CallFlowGroupView callFlowGroupView = new CallFlowGroupView(flow, callEventView);
        callFlowGroupViews.add(callFlowGroupView);
    }

    private String getFlow(CallEventView callEventView) {
        String callState = callEventView.getCallState();
        if (callEventView.isMissedCall()) {
            missed = true;
            return CallTypeConstants.MISSED;
        } else if (callState.equals(CallState.HEALTH_TIPS.name())) {
            authenticated = true;
            return CallTypeConstants.HEALTH_TIPS;
        } else if (callState.equals(CallState.OUTBOX.name())) {
            authenticated = true;
            return CallTypeConstants.OUTBOX_CALL;
        } else {
            for (String key : TREE_TO_FLOW_MAP.keySet()) {
                if (key.equals(callEventView.getTree())) {
                    authenticated = true;
                    return TREE_TO_FLOW_MAP.get(key);
                }
            }
        }
        return CallTypeConstants.MENU;
    }

    private void addEventToLastFlow(CallEventView callEventView) {
        if (!callFlowGroupViews.isEmpty()) {
            getLastCallFlowGroupView().add(callEventView);
        }
    }

    private String getFlowOfLastCallFlowGroup() {
        if (callFlowGroupViews.isEmpty()) return null;
        return getLastCallFlowGroupView().getFlow();
    }

    private CallFlowGroupView getLastCallFlowGroupView() {
        return callFlowGroupViews.get(callFlowGroupViews.size() - 1);
    }
}

