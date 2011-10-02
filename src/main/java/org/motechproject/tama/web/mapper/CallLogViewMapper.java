package org.motechproject.tama.web.mapper;

import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.repository.AllPatients;
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
        for (CallLog callLog : callLogs) {
            String patientId = allPatients.get(callLog.getPatientDocumentId()).getPatientId();
            CallLogView callLogView = new CallLogView(patientId, callLog);
            callLogViews.add(callLogView);
        }
        return callLogViews;

    }
}
