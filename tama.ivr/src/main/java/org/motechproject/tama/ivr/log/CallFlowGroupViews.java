package org.motechproject.tama.ivr.log;


import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.tama.ivr.log.CallFlowConstants.TREE_TO_FLOW_MAP;

public class CallFlowGroupViews {

    private CallLog callLog;
    private String flows;
    private List<CallFlowGroupView> callFlowGroupViews;
    private boolean authenticated;
    private boolean missed;

    public CallFlowGroupViews(CallLog callLog) {
        this.callLog = callLog;
        callFlowGroupViews = new ArrayList<>();
        initializeCallFlowGroups();
    }

    public List<CallFlowGroupView> getCallFlowGroupViews() {
        List<CallFlowGroupView> views = new ArrayList<>();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            if (!missed && callFlowGroupView.getFlowDuration() == 0) continue;
            views.add(callFlowGroupView);
        }
        return views;
    }

    public String getFlows() {
        return flows;
    }

    public boolean hasMissedEvent() {
        return missed;
    }

    public boolean hasAuthenticationEvent() {
        return authenticated;
    }

    private void initializeCallFlowGroups() {
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
        Set<String> flowsInViews = new HashSet<>();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            String callFlowGroupViewFlow = callFlowGroupView.getFlow();
            flowsInViews.add(callFlowGroupViewFlow);
        }
        if (authenticated) {
            flows = StringUtils.join(flowsInViews, ", ");
        } else if (missed) {
            flows = getCurrentCallFlowGroupView().toString();
        } else {
            flows = CallTypeConstants.NO_RESPONSE;
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
        } else if (callState.equals(CallState.PULL_MESSAGES.name())) {
            authenticated = true;
            return CallTypeConstants.MESSAGES;
        } else if (callState.equals(CallState.PUSH_MESSAGES.name())) {
            authenticated = true;
            return CallTypeConstants.PUSHED_MESSAGES;
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
}
