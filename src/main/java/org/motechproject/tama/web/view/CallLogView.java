package org.motechproject.tama.web.view;

import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.ivr.logging.domain.CallLog;

import java.util.ArrayList;
import java.util.List;

public class CallLogView {

    private String patientId;

    private CallLog callLog;

    public CallLogView(String patientId, CallLog callLog) {
        this.patientId = patientId;
        this.callLog = callLog;
    }

    public String getPatientId() {
        return patientId;
    }

    public CallLog getCallLog() {
        return callLog;
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
            return "Tama called " + patientId;
        } else {
            return patientId + " called Tama";
        }

    }
}

