package org.motechproject.tama.web.model;

import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.log.CallFlowGroupView;
import org.motechproject.tama.web.view.CallLogView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tama.ivr.log.CallFlowConstants.TREE_TO_FLOW_MAP;

public class CallFlowDetailMap {
    private Map<String, CallFlowDetails> callFlowDetailsMap = new HashMap<String, CallFlowDetails>();

    public CallFlowDetailMap() {
        for (String tree : TREE_TO_FLOW_MAP.keySet()) {
            String flow = TREE_TO_FLOW_MAP.get(tree);
            callFlowDetailsMap.put(flow, new CallFlowDetails());
        }
        callFlowDetailsMap.put(CallTypeConstants.HEALTH_TIPS, new CallFlowDetails());
    }

    public void populateFlowDetails(List<CallLogView> callLogViews) {
        if (callLogViews.isEmpty() || callLogViews.get(0).isMissedCall()) return;
        List<CallFlowGroupView> callFlowGroupViews = callLogViews.get(0).getCallFlowGroupViews();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            callFlowDetailsMap.get(callFlowGroupView.getFlow()).flowAccessed(callFlowGroupView.getFlowDuration());
        }
    }

    public Map<String, CallFlowDetails> getCallFlowDetailsMap() {
        return callFlowDetailsMap;
    }
}
