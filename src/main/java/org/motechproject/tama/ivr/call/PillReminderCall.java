package org.motechproject.tama.ivr.call;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.eventlogging.EventDataBuilder;
import org.motechproject.tama.eventlogging.EventLogConstants;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
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
    private EventService eventService;

    @Autowired
    public PillReminderCall(CallService callService, EventService eventService, AllPatients allPatients) {
        this.callService = callService;
        this.allPatients = allPatients;
        this.eventService = eventService;
    }

    public void execute(String patientId, final String dosageId, final int timesSent, final int totalTimesToSend) {
        Map<String, String> params = new HashMap<String, String>() {{
            put(DOSAGE_ID, dosageId);
            put(TIMES_SENT, String.valueOf(timesSent));
            put(TOTAL_TIMES_TO_SEND, String.valueOf(totalTimesToSend));
            put(TAMAConstants.IS_OUTBOUND_CALL, "true");
        }};
        makeCall(patientId, params);
    }

    private void makeCall(String patientId, Map<String, String> params) {
        Patient patient = allPatients.get(patientId);
        if (patient == null || patient.isNotActive()) return;
        EventDataBuilder eventDataBuilder = new EventDataBuilder(null, "Dial", patient.getId(), EventLogConstants.CALL_TYPE_PILL_REMINDER, DateUtil.now());
        eventDataBuilder.withCallDirection(EventLogConstants.CALL_DIRECTION_OUTBOUND)
        	.withCallerId(patient.getIVRMobilePhoneNumber());
		eventService.publishEvent(eventDataBuilder.build());
        callService.dial(patient.getIVRMobilePhoneNumber(), params);
    }
}
