package org.motechproject.tama.ivr.call;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PillReminderCall {
    public static final String DOSAGE_ID = "dosage_id";
    private Patients patients;
    private CallService callService;

    @Autowired
    public PillReminderCall(CallService callService, Patients patients) {
        this.callService = callService;
        this.patients = patients;
    }

    public void execute(String patientDocId, String dosageId) {
        Patient patient = patients.get(patientDocId);
        if (patient == null || patient.isNotActive()) return;

        Map<String, String> params = new HashMap<String, String>();
        params.put(DOSAGE_ID, dosageId);

        callService.call(patient.getIVRMobilePhoneNumber(), params);
    }

}
