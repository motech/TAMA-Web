package org.motechproject.tama.outbox.service;

import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.Outbox;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus.PENDING;

@Service
public class OutboxService implements Outbox {

    private AllPatients allPatients;
    private IVRCall ivrCall;
    private VoiceOutboxService voiceOutboxService;
    private OutboxSchedulerService outboxSchedulerService;
    private OutboxEventHandler outboxEventHandler;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OutboxService(AllPatients allPatients, @Qualifier("IVRCall") IVRCall ivrCall, VoiceOutboxService voiceOutboxService,
                         OutboxSchedulerService outboxSchedulerService, OutboxEventHandler outboxEventHandler, OutboxRegistry outboxRegistry) {
        this.allPatients = allPatients;
        this.ivrCall = ivrCall;
        this.voiceOutboxService = voiceOutboxService;
        this.outboxSchedulerService = outboxSchedulerService;
        this.outboxEventHandler = outboxEventHandler;
        outboxRegistry.registerOutbox(this);
    }

    public void enroll(Patient patient) {
        if (patient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxSchedulerService.scheduleOutboxJobs(patient);
        }
    }

    public void disEnroll(Patient dbPatient) {
        if (dbPatient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxSchedulerService.unscheduleOutboxJobs(dbPatient);
        }
    }

    public void reEnroll(Patient dbPatient, Patient patient) {
        disEnroll(dbPatient);
        enroll(patient);
    }

    public boolean hasPendingOutboxMessages(String patientDocumentId) {
        return voiceOutboxService.getNumberOfMessages(patientDocumentId, PENDING) != 0;
    }

    public boolean hasPendingOutboxMessages(String patientDocumentId, String voiceMessageTypeName) {
        return voiceOutboxService.getNumberOfMessages(patientDocumentId, PENDING, voiceMessageTypeName) != 0;
    }

    public boolean hasMessages(String patientDocumentId, String voiceMessageType, DateTime creationTime) {
        return voiceOutboxService.getNumberOfMessages(patientDocumentId, voiceMessageType, creationTime.toDate()) != 0;
    }

    public String addMessage(String patientId, String voiceMessageTypeName) {
        return addMessage(patientId, voiceMessageTypeName, new HashMap<String, Object>());
    }

    public String addMessage(String patientId, String voiceMessageTypeName, Map<String, Object> parameterMap) {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setExternalId(patientId);
        voiceMessage.setParameters(parameterMap);
        voiceMessage.setExpirationDate(DateUtil.today().plusWeeks(1).toDate());
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(voiceMessageTypeName);
        voiceMessage.setVoiceMessageType(voiceMessageType);
        voiceOutboxService.addMessage(voiceMessage);
        outboxEventHandler.onCreate(voiceMessage);
        return voiceMessage.getId();
    }

    public void call(MotechEvent event) {
        String patientDocumentId = (String) event.getParameters().get(OutboxSchedulerService.EXTERNAL_ID_KEY);
        boolean scheduleRepeatingJobs = !"true".equals(event.getParameters().get(OutboxSchedulerService.IS_RETRY));
        call(allPatients.get(patientDocumentId), scheduleRepeatingJobs);
    }

    void call(Patient patient, boolean scheduleRepeatingJobs) {
        try {
            if (patient != null && patient.allowOutboxCalls() && hasPendingOutboxMessages(patient.getId())) {
                if (scheduleRepeatingJobs) {
                    outboxSchedulerService.scheduleRepeatingJobForOutBoxCall(patient);
                }
                Map<String, String> callParams = new HashMap<String, String>();
                callParams.put(TAMAIVRContext.IS_OUTBOX_CALL, "true");
                ivrCall.makeCall(patient, CallTypeConstants.OUTBOX_CALL, callParams);
            }
        } catch (Exception e) {
            logger.error("Failed to handle OutboxCall event", e);
        }
    }
}