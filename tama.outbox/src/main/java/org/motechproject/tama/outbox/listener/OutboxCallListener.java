package org.motechproject.tama.outbox.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.outbox.service.OutboxSchedulerService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class OutboxCallListener {

    private VoiceOutboxService voiceOutboxService;
    private OutboxSchedulerService outboxSchedulerService;
    private AllPatients allPatients;
    private IVRCall ivrCall;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OutboxCallListener(VoiceOutboxService voiceOutboxService, AllPatients allPatients, @Qualifier("IVRCall") IVRCall ivrCall, OutboxSchedulerService outboxSchedulerService) {
        this.voiceOutboxService = voiceOutboxService;
        this.ivrCall = ivrCall;
        this.outboxSchedulerService = outboxSchedulerService;
        this.allPatients = allPatients;
    }

    @MotechListener(subjects = TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT)
    public void handleOutBoxCall(MotechEvent event) {
        Map<String, Object> parameters = event.getParameters();
        String externalId = (String) parameters.get(OutboxSchedulerService.EXTERNAL_ID_KEY);

        Patient patient = allPatients.get(externalId);
        if (patient != null && patient.allowOutboxCalls()) {
            try {
                int numberPendingMessages = voiceOutboxService.getNumberPendingMessages(externalId);
                if (numberPendingMessages > 0) {
                    Map<String, String> callParams = new HashMap<String, String>();
                    callParams.put(TAMAIVRContext.IS_OUTBOX_CALL, "true");
                    if (!"true".equals(event.getParameters().get(OutboxSchedulerService.IS_RETRY))) {
                        outboxSchedulerService.scheduleRepeatingJobForOutBoxCall(patient);
                    }
                    ivrCall.makeCall(patient, callParams);
                }
            } catch (Exception e) {
                logger.error("Failed to handle OutboxCall event, this event would not be retried but the subsequent repeats would happen.", e);
            }
        }
    }
}
