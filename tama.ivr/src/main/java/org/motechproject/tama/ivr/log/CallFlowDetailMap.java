package org.motechproject.tama.ivr.log;

import org.motechproject.tama.common.CallTypeConstants;

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
        callFlowDetailsMap.put(CallTypeConstants.MESSAGES, new CallFlowDetails());
    }

    public void populateFlowDetails(CallFlowGroupViews flowGroupViews) {
        if (null == flowGroupViews || flowGroupViews.hasMissedEvent()) return;
        List<CallFlowGroupView> callFlowGroupViews = flowGroupViews.getCallFlowGroupViews();
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            callFlowDetailsMap.get(callFlowGroupView.getFlow()).flowAccessed(callFlowGroupView.getFlowDuration());
            callFlowDetailsMap.get(callFlowGroupView.getFlow()).respondedWith(callFlowGroupView.getAllResponses());
        }
    }

    public Map<String, CallFlowDetails> getCallFlowDetailsMap() {
        return callFlowDetailsMap;
    }
}
