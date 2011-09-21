package org.motechproject.tama.ivr.call;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.eventlogging.EventDataBuilder;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PillReminderCall {
    public static final String DOSAGE_ID = "dosage_id";
    public static final String TIMES_SENT = "times_sent";
    public static final String TOTAL_TIMES_TO_SEND = "total_times_to_send";
    public static final String APPLICATION_URL = "application.url";

    private AllPatients allPatients;
    private IVRService ivrService;
    private EventService eventService;

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    @Autowired
    public PillReminderCall(IVRService callService, EventService eventService, AllPatients allPatients) {
        this.ivrService = callService;
        this.allPatients = allPatients;
        this.eventService = eventService;
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
        // TODO: Move this to platform
        EventDataBuilder eventDataBuilder = new EventDataBuilder(null, "Dial", patient.getId(), new HashMap<String, String>(), DateUtil.now());
        eventDataBuilder.withCallDirection(IVRRequest.CallDirection.Outbound)
                .withCallerId(patient.getIVRMobilePhoneNumber());
        eventService.publishEvent(eventDataBuilder.build());
        CallRequest callRequest = new CallRequest(patient.getIVRMobilePhoneNumber(), params, getApplicationUrl());
        ivrService.initiateCall(callRequest);
    }

    String getApplicationUrl() {
        return (String) properties.get(APPLICATION_URL);
    }
}
