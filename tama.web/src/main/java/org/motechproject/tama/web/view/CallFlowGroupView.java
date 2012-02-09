package org.motechproject.tama.web.view;

import java.util.ArrayList;
import java.util.List;

public class CallFlowGroupView {

    private String flow;

    private List<CallEventView> callEventViews;

    public CallFlowGroupView(String flow, CallEventView callEventView) {
        this.flow = flow;
        callEventViews = new ArrayList<CallEventView>();
        add(callEventView);
    }

    public String getFlow() {
        return flow;
    }

    public List<CallEventView> getCallEventViews() {
        return callEventViews;
    }

    public void add(CallEventView callEventView) {
        callEventViews.add(callEventView);
    }

    public List<String> getAllResponses() {
        List<String> responseList = new ArrayList<String>();
        for (CallEventView callEventView : callEventViews) {
            responseList.addAll(callEventView.getResponses());
        }
        return responseList;
    }
}