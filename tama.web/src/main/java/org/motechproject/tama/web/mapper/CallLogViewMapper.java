package org.motechproject.tama.web.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.web.view.CallLogView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallLogViewMapper {

    private AllPatients allPatients;
    private Patients allLoadedPatients = new Patients();

    @Autowired
    public CallLogViewMapper(AllPatients allPatients) {
        this(allPatients, new Patients());
    }

    public CallLogViewMapper(AllPatients allPatients, Patients allLoadedPatients) {
        this.allPatients = allPatients;
        this.allLoadedPatients = allLoadedPatients;
    }

    public List<CallLogView> toCallLogView(List<CallLog> callLogs) {
        List<CallLogView> callLogViews = new ArrayList<CallLogView>();
        for (CallLog callLog : callLogs) {
            String patientDocumentId = callLog.getPatientDocumentId();
            List<String> likelyPatientDocIds = callLog.getLikelyPatientIds();
            if (StringUtils.isEmpty(patientDocumentId) && CollectionUtils.isEmpty(likelyPatientDocIds)) continue;

            Patient patient = patientDocumentId == null ? null : getPatient(patientDocumentId);
            String patientId = patient == null ? "" : patient.getPatientId();
            String clinicName = patient == null ? getPatient(likelyPatientDocIds.get(0)).getClinic().getName() : patient.getClinic().getName();
            callLogViews.add(new CallLogView(patientId, callLog, clinicName, getLikelyPatientIds(likelyPatientDocIds)));
        }
        return callLogViews;
    }

    private List<String> getLikelyPatientIds(List<String> likelyPatientDocIds) {
        ArrayList<String> likelyPatientIds = new ArrayList<String>();
        for (String likelyPatientDocId : likelyPatientDocIds) {
            likelyPatientIds.add(getPatient(likelyPatientDocId).getPatientId());
        }
        return likelyPatientIds;
    }

    private Patient getPatient(String patientDocumentId) {
        return allLoadedPatients.isEmpty() ? allPatients.get(patientDocumentId) : allLoadedPatients.getBy(patientDocumentId);
    }
}
