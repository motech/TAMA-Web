package org.motechproject.tama.outbox.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.handler.OutboxHandler;
import org.motechproject.tama.outbox.service.OutboxSchedulerService;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class OutboxCallListener {

    private AllPatients allPatients;
    private Map<CallPreference, OutboxHandler> outboxHandlers = new HashMap<CallPreference, OutboxHandler>();

    @Autowired
    public OutboxCallListener(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public void register(CallPreference callPreference, OutboxHandler outboxHandler) {
        outboxHandlers.put(callPreference, outboxHandler);
    }

    @MotechListener(subjects = TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT)
    public void handle(MotechEvent event) {
        String patientDocId = (String) event.getParameters().get(OutboxSchedulerService.EXTERNAL_ID_KEY);
        CallPreference callPreference = allPatients.get(patientDocId).getPatientPreferences().getCallPreference();
        outboxHandlers.get(callPreference).handle(event);
    }
}
