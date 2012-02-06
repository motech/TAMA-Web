package org.motechproject.tama.outbox.service;

import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.call.IVRCall;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.strategy.Outbox;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OutboxService implements Outbox {

    private AllPatients allPatients;
    private IVRCall ivrCall;
    private VoiceOutboxService voiceOutboxService;
    private OutboxSchedulerService outboxSchedulerService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OutboxService(AllPatients allPatients, @Qualifier("IVRCall") IVRCall ivrCall, VoiceOutboxService voiceOutboxService, OutboxSchedulerService outboxSchedulerService, PatientService patientService) {
        this.allPatients = allPatients;
        this.ivrCall = ivrCall;
        this.voiceOutboxService = voiceOutboxService;
        this.outboxSchedulerService = outboxSchedulerService;
        patientService.registerOutbox(this);
    }

    public void enroll(Patient patient) {
        if (patient.isOnDailyPillReminder() && patient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxSchedulerService.scheduleOutboxJobs(patient);
        }
    }

    void disEnroll(Patient dbPatient) {
        if (dbPatient.isOnDailyPillReminder() && dbPatient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxSchedulerService.unscheduleOutboxJobs(dbPatient);
        }
    }

    public void reEnroll(Patient dbPatient, Patient patient) {
        disEnroll(dbPatient);
        enroll(patient);
    }

    public boolean hasPendingOutboxMessages(String patientDocumentId) {
        return voiceOutboxService.getNumberPendingMessages(patientDocumentId) != 0;
    }

    public void addMessage(String patientId) {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setPartyId(patientId);
        voiceMessage.setExpirationDate(DateUtil.today().plusWeeks(1).toDate());
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setPriority(MessagePriority.MEDIUM);
        voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        voiceMessage.setVoiceMessageType(voiceMessageType);
        voiceOutboxService.addMessage(voiceMessage);
    }

    public void call(MotechEvent event) {
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