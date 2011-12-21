package org.motechproject.tama.dailypillreminder.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdherenceTrendListener {
    private VoiceOutboxService outboxService;
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private AllPatients allPatients;

    @Autowired
    public AdherenceTrendListener(VoiceOutboxService outboxService, DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, AllPatients allPatients) {
        this.outboxService = outboxService;
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.allPatients = allPatients;
    }

    @MotechListener(subjects = TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT)
    public void handleAdherenceTrendEvent(MotechEvent motechEvent) {
        String externalId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = allPatients.get(externalId);

        if (patient != null && patient.allowAdherenceCalls()) {
            OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
            voiceMessage.setPartyId(externalId);
            voiceMessage.setExpirationDate(DateUtil.today().plusWeeks(1).toDate());
            VoiceMessageType voiceMessageType = new VoiceMessageType();
            voiceMessageType.setPriority(MessagePriority.MEDIUM);
            voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
            voiceMessage.setVoiceMessageType(voiceMessageType);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(TAMAConstants.VOICE_MESSAGE_COMMAND, Arrays.asList("weeklyAdherenceOutBoxCommand"));
            voiceMessage.setParameters(parameters);
            outboxService.addMessage(voiceMessage);

            dailyReminderAdherenceTrendService.raiseAlertIfAdherenceTrendIsFalling(externalId, DateUtil.now());
        }
    }
}