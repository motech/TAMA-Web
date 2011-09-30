package org.motechproject.tama.web.mapper;

import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.web.view.CallEventView;
import org.motechproject.tama.web.view.CallLogView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallLogViewMapper {

    private AllPatients allPatients;

    @Autowired
    public CallLogViewMapper(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public List<CallLogView> toCallLogView(List<CallLog> callLogs) {
        List<CallLogView> callLogViews = new ArrayList<CallLogView>();
        for(CallLog callLog : callLogs){
            CallLogView callLogView = new CallLogView();
            setCallLogViewDetails(callLog, callLogView);
            callLogViews.add(callLogView);
        }
        return callLogViews;

    }

    private void setCallLogViewDetails(CallLog callLog, CallLogView callLogView) {
        List<CallEventView> callEventViews = new ArrayList<CallEventView>();
        callLogView.setCallType(callLog.getCallType());
        callLogView.setPatientId(allPatients.get(callLog.getPatientDocumentId()).getPatientId());
        callLogView.setCallDirection(callLog.getCallDirection());
        callLogView.setCallId(callLog.getCallId());
        callLogView.setStartTime(callLog.getStartTime());
        callLogView.setEndTime(callLog.getEndTime());
        callLogView.setPhoneNumber(callLog.getPhoneNumber());
        for(CallEvent callEvent : callLog.getCallEvents()){
            CallEventView callEventView = new CallEventView();
            callEventView.setName(callEvent.getName());
            callEventView.setData(callEvent.getData());
            callEventViews.add(callEventView);
        }
        callLogView.setCallEvents(callEventViews);
    }
}
