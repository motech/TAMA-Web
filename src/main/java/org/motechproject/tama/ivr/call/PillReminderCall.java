package org.motechproject.tama.ivr.call;

import org.motechproject.server.service.ivr.IVRService;
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

    @Autowired
    public PillReminderCall(IVRService ivrService, AllPatients allPatients, @Qualifier("ivrProperties")Properties properties) {
        super(allPatients, ivrService, properties);
    }

    public void execute(String patientDocId, final String dosageId, final int timesSent, final int totalTimesToSend) {
        Map<String, String> params = new HashMap<String, String>() {{
            put(DOSAGE_ID, dosageId);
            put(TIMES_SENT, String.valueOf(timesSent));
            put(TOTAL_TIMES_TO_SEND, String.valueOf(totalTimesToSend));
        }};
        makeCall(patientDocId, params);
    }
}
