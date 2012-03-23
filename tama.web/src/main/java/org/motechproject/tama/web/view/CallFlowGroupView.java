package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.ArrayList;
import java.util.List;

public class CallFlowGroupView {

    private String flow;
    private List<CallEventView> callEventViews;
    private DateTime flowStartTime;
    private DateTime flowEndTime;

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

    public String missedCallType() {
        for (CallEventView callEventView : callEventViews) {
            if (callEventView.isMissedCall())
                return callEventView.getCallType();
        }
        return null;
    }

    public List<String> getAllResponses() {
        List<String> responseList = new ArrayList<String>();
        for (CallEventView callEventView : callEventViews) {
            responseList.addAll(callEventView.getResponses());
        }
        return responseList;
    }

    public void setFlowStartTime(DateTime flowStartTime) {
        this.flowStartTime = flowStartTime;
    }

    public void setFlowEndTime(DateTime flowEndTime) {
        this.flowEndTime = flowEndTime;
    }

    public String getFlowDuration() {
        int flowDurationInSeconds = new Period(flowStartTime, flowEndTime, PeriodType.seconds()).getSeconds();
        int flowDurationInMinutes = flowDurationInSeconds / 60;
        flowDurationInSeconds = flowDurationInSeconds % 60;
        return String.format("%s min %s sec",String.valueOf(flowDurationInMinutes), String.valueOf(flowDurationInSeconds));
    }

    public int getFlowDurationInSeconds() {
        return new Period(flowStartTime, flowEndTime, PeriodType.seconds()).getSeconds();
    }
}