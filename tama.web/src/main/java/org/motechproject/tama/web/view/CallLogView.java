package org.motechproject.tama.web.view;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.tama.web.view.CallFlowConstants.TREE_TO_FLOW_MAP;

public class CallLogView {

    private String patientId;
    private CallLog callLog;
    private String clinicName;
    private List<String> likelyPatientIds;
    private String callDate;
    private LocalTime callStartTime;
    private LocalTime callEndTime;
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
        setCallDate();
        setCallStartTime();
        setCallEndTime();
        setCallFlowGroupViews();
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
        List<CallFlowGroupView> views = new ArrayList<CallFlowGroupView>();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            if (!missed && callFlowGroupView.getFlowDuration() == 0) continue;
            views.add(callFlowGroupView);
        }
        return views;
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
            if (flowToWhichCallEventBelongs.equals(getFlowOfCurrentCallFlowGroup())) {
                addEventToCurrentFlow(callEventView);
            } else {
                createNewFlow(flowToWhichCallEventBelongs, callEventView);
                setFlowEndTimeForLastFlow(callEventView.getTimeStamp());
            }
        }
    }

    private void setFlowEndTimeForLastFlow(DateTime flowEndTimeForLastToLastFlow) {
        CallFlowGroupView lastCallFlowGroupView = getLastCallFlowGroupView();
        if (lastCallFlowGroupView != null) {
            lastCallFlowGroupView.setFlowEndTime(flowEndTimeForLastToLastFlow);
        }
    }

    private CallFlowGroupView getLastCallFlowGroupView() {
        int listSize = callFlowGroupViews.size();
        return listSize > 1 ? callFlowGroupViews.get(listSize - 2) : null;
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
            flows = getCurrentCallFlowGroupView().toString();
        } else{
            flows = CallTypeConstants.UNAUTHENTICATED;
        }
    }

    private void createNewFlow(String flow, CallEventView callEventView) {
        CallFlowGroupView callFlowGroupView = new CallFlowGroupView(flow, callEventView);
        callFlowGroupView.setFlowStartTime(callEventView.getTimeStamp());
        callFlowGroupView.setFlowEndTime(callLog.getEndTime());
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

    private void addEventToCurrentFlow(CallEventView callEventView) {
        if (!callFlowGroupViews.isEmpty()) {
            getCurrentCallFlowGroupView().add(callEventView);
        }
    }

    private String getFlowOfCurrentCallFlowGroup() {
        if (callFlowGroupViews.isEmpty()) return null;
        return getCurrentCallFlowGroupView().getFlow();
    }

    private CallFlowGroupView getCurrentCallFlowGroupView() {
        return callFlowGroupViews.get(callFlowGroupViews.size() - 1);
    }

    public boolean isMissedCall() {
        return missed;
    }
}

