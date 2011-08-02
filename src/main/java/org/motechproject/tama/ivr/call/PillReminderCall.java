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
    public static final String REGIMEN_ID = "regimen_id";
    public static final String TIMES_SENT = "times_sent";
    public static final String TOTAL_TIMES_TO_SEND = "total_times_to_send";

    private Patients patients;
    private CallService callService;

    @Autowired
    public PillReminderCall(CallService callService, Patients patients) {
        this.callService = callService;
        this.patients = patients;
    }

    public void execute(String patientId, final String regimenId, final String dosageId, final int timesSent, final int totalTimesToSend) {
        Map<String, String> params = new HashMap<String, String>() {{
            put(REGIMEN_ID, regimenId);
            put(DOSAGE_ID, dosageId);
            put(TIMES_SENT, String.valueOf(timesSent));
            put(TOTAL_TIMES_TO_SEND, String.valueOf(totalTimesToSend));
        }};
        makeCall(patientId, params);
    }

    private void makeCall(String patientId, Map<String, String> params) {
        Patient patient = patients.get(patientId);
        if (patient == null || patient.isNotActive()) return;

        callService.dial(patient.getIVRMobilePhoneNumber(), params);
    }
}
