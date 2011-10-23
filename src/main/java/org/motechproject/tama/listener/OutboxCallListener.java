package org.motechproject.tama.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.call.IvrCall;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.repository.AllPatients;
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

    private TAMASchedulerService tamaSchedulerService;

    @Autowired
    public OutboxCallListener(VoiceOutboxService voiceOutboxService, IVRService ivrService, AllPatients allPatients, @Qualifier("ivrProperties") Properties properties, TAMASchedulerService tamaSchedulerService) {
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
        int numberPendingMessages = voiceOutboxService.getNumberPendingMessages(externalId);
        if (numberPendingMessages > 0) {
            IvrCall ivrCall = new IvrCall(allPatients, ivrService, properties);
            Map<String, String> callParams = new HashMap<String, String>();
            callParams.put(TAMAIVRContext.IS_OUTBOX_CALL, "true");
            ivrCall.makeCall(externalId, callParams);
            if (!"true".equals(event.getParameters().get(TAMASchedulerService.IS_RETRY)))
                tamaSchedulerService.scheduleRepeatingJobForOutBoxCall(allPatients.get(externalId));
        }
    }
}
