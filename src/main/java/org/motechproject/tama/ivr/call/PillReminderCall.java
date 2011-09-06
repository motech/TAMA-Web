package org.motechproject.tama.ivr.call;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PillReminderCall {
    public static final String DOSAGE_ID = "dosage_id";
    public static final String TIMES_SENT = "times_sent";
    public static final String TOTAL_TIMES_TO_SEND = "total_times_to_send";

    private AllPatients allPatients;
    private CallService callService;

    @Autowired
    public PillReminderCall(CallService callService, AllPatients allPatients) {
        this.callService = callService;
        this.allPatients = allPatients;
    }

    public void execute(String patientId, final String dosageId, final int timesSent, final int totalTimesToSend) {
        Map<String, String> params = new HashMap<String, String>() {{
            put(DOSAGE_ID, dosageId);
            put(TIMES_SENT, String.valueOf(timesSent));
            put(TOTAL_TIMES_TO_SEND, String.valueOf(totalTimesToSend));
        }};
        makeCall(patientId, params);
    }

    private void makeCall(String patientId, Map<String, String> params) {
        Patient patient = allPatients.get(patientId);
        if (patient == null || patient.isNotActive()) return;

        callService.dial(patient.getIVRMobilePhoneNumber(), params);
    }
}
