package org.motechproject.tama.ivr.call;

import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PillReminderCall extends IvrCall {
    public static final String DOSAGE_ID = "dosage_id";
    public static final String TIMES_SENT = "times_sent";
    public static final String TOTAL_TIMES_TO_SEND = "total_times_to_send";
    public static final String RETRY_INTERVAL = "retry_interval";
    private AllPatients allPatients;

    @Autowired
    public PillReminderCall(IVRService ivrService, AllPatients allPatients, @Qualifier("ivrProperties") Properties properties) {
        super(ivrService, properties);
        this.allPatients = allPatients;
    }

    public void execute(String patientDocId, final String dosageId, final int timesSent, final int totalTimesToSend, final int retryInterval) {
        final Patient patient = allPatients.get(patientDocId);
        if (patient != null && patient.allowAdherenceCalls()) {
            Map<String, String> params = new HashMap<String, String>() {{
                put(DOSAGE_ID, dosageId);
                put(TIMES_SENT, String.valueOf(timesSent));
                put(TOTAL_TIMES_TO_SEND, String.valueOf(totalTimesToSend));
                put(RETRY_INTERVAL, String.valueOf(retryInterval));
            }};
            makeCall(patient, params);
        }
    }
}