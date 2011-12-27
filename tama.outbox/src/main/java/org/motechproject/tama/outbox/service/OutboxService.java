package org.motechproject.tama.outbox.service;

import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.strategy.Outbox;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutboxService implements Outbox {

    private VoiceOutboxService voiceOutboxService;
    private OutboxSchedulerService outboxSchedulerService;

    @Autowired
    public OutboxService(VoiceOutboxService voiceOutboxService, OutboxSchedulerService outboxSchedulerService, PatientService patientService) {
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
}