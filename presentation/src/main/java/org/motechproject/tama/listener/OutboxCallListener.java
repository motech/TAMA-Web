package org.motechproject.tama.listener;

import org.motechproject.ivr.service.IVRService;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.call.IvrCall;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Service
public class OutboxCallListener {

    private VoiceOutboxService voiceOutboxService;
    private IVRService ivrService;
    private AllPatients allPatients;
    private Properties properties;

    private TamaSchedulerService tamaSchedulerService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OutboxCallListener(VoiceOutboxService voiceOutboxService, IVRService ivrService, AllPatients allPatients, @Qualifier("ivrProperties") Properties properties, TamaSchedulerService tamaSchedulerService) {
        this.voiceOutboxService = voiceOutboxService;
        this.ivrService = ivrService;
        this.allPatients = allPatients;
        this.properties = properties;
        this.tamaSchedulerService = tamaSchedulerService;
    }

    @MotechListener(subjects = TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT)
    public void handleOutBoxCall(MotechEvent event) {
        Map<String, Object> parameters = event.getParameters();
        String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);

        Patient patient = allPatients.get(externalId);
        if (patient != null && patient.allowOutboxCalls()) {
            try {
                int numberPendingMessages = voiceOutboxService.getNumberPendingMessages(externalId);
                if (numberPendingMessages > 0) {
                    IvrCall ivrCall = new IvrCall(ivrService, properties);
                    Map<String, String> callParams = new HashMap<String, String>();
                    callParams.put(TAMAIVRContext.IS_OUTBOX_CALL, "true");
                    ivrCall.makeCall(patient, callParams);
                    if (!"true".equals(event.getParameters().get(TamaSchedulerService.IS_RETRY))) {
                        tamaSchedulerService.scheduleRepeatingJobForOutBoxCall(patient);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to handle OutboxCall event, this event would not be retried but the subsequent repeats would happen.", e);
            }
        }
    }
}
