package org.motechproject.tama.web.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.tamacallflow.domain.CallLog;
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
            String patientDocumentId = callLog.getPatientDocumentId();
            List<String> likelyPatientDocIds = callLog.getLikelyPatientIds();
            if (StringUtils.isEmpty(patientDocumentId) && CollectionUtils.isEmpty(likelyPatientDocIds)) continue;

            Patient patient = patientDocumentId == null ? null : allPatients.get(patientDocumentId);
            String patientId = patient == null ? "" : patient.getPatientId();
            String clinicName = patient == null ? allPatients.get(likelyPatientDocIds.get(0)).getClinic().getName() : patient.getClinic().getName();
            callLogViews.add(new CallLogView(patientId, callLog, clinicName, getLikelyPatientIds(likelyPatientDocIds)));
        }
        return callLogViews;
    }

    private List<String> getLikelyPatientIds(List<String> likelyPatientDocIds) {
        ArrayList<String> likelyPatientIds = new ArrayList<String>();
        for (String likelyPatientDocId : likelyPatientDocIds) {
            likelyPatientIds.add(allPatients.get(likelyPatientDocId).getPatientId());
        }
        return likelyPatientIds;
    }
}
