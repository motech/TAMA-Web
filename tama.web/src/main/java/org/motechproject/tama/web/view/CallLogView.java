package org.motechproject.tama.web.view;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;

import java.util.*;

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

    private void createNewFlow(String flow, CallEventView callEventView) {
        CallFlowGroupView callFlowGroupView = new CallFlowGroupView(flow, callEventView);
        callFlowGroupViews.add(callFlowGroupView);

    }

    private void addEventToLastFlow(CallEventView callEventView) {
        if (!callFlowGroupViews.isEmpty()) {
            getLastCallFlowGroupView().add(callEventView);
        }
    }

    private CallFlowGroupView getLastCallFlowGroupView() {
        return callFlowGroupViews.get(callFlowGroupViews.size() - 1);
    }

    public void setCallFlowGroupViews(){
        for (CallEvent callEvent : callLog.getCallEvents()) {
            CallEventView callEventView = new CallEventView(callEvent);
            String flowToWhichCallEventBelongs = getFlow(callEventView);
            if (flowToWhichCallEventBelongs.equals(getFlowOfLastCallFlowGroup())) {
                addEventToLastFlow(callEventView);
            } else {
                createNewFlow(flowToWhichCallEventBelongs, callEventView);
            }
        }
        Set<String> flowsInViews = new HashSet<String> ();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            String callFlowGroupViewFlow = callFlowGroupView.getFlow();
            flowsInViews.add(callFlowGroupViewFlow);
        }
        if(authenticated) {
            flows = StringUtils.join(flowsInViews, ", ");
        } else{
            flows = "Unauthenticated";
        }
    }

    public List<CallFlowGroupView> getCallFlowGroupViews() {
        return callFlowGroupViews;
    }

    public String getFlows(){
        return flows;
    }

    private String getFlow(CallEventView callEventView) {
        return getFlow(callEventView.getTree(), callEventView.getCallState());
    }

    public String getTitle() {
        String patientInfo = StringUtils.isEmpty(patientId) ? StringUtils.join(likelyPatientIds, " or ") : patientId;
        if (callLog.getCallDirection() == CallDirection.Outbound) {
            return "Tama called " + patientInfo + " || Clinic :" + clinicName;
        } else {
            return patientInfo + " called Tama" + " || Clinic :" + clinicName;
        }
    }

    private String getFlow(String tree, String callState) {
        if (callState.equals(CallState.HEALTH_TIPS.name())) {
            authenticated = true;
            return CallFlowConstants.HEALTH_TIPS;
        } else if (callState.equals(CallState.OUTBOX.name())) {
            authenticated = true;
            return CallFlowConstants.OUTBOX;
        } else {
            List<String> listOfTrees;
            Map<String, List<String>> treeToFlowMap = CallFlowConstants.treeToFlowMap;
            for (String key : treeToFlowMap.keySet()) {
                listOfTrees = treeToFlowMap.get(key);
                if (listOfTrees.contains(tree)) {
                    authenticated = true;
                    return key;
                }
            }
        }
        return CallFlowConstants.MENU;
    }

    private String getFlowOfLastCallFlowGroup() {
        if (callFlowGroupViews.isEmpty()) return null;
        return getLastCallFlowGroupView().getFlow();
    }
}

