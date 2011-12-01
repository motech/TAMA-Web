package org.motechproject.tamacallflow.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.controller.OutboxController;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdherenceTrendListener {
    private VoiceOutboxService outboxService;
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private AllPatients allPatients;

    @Autowired
    public AdherenceTrendListener(VoiceOutboxService outboxService, DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, AllPatients allPatients) {
        this.outboxService = outboxService;
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.allPatients = allPatients;
    }

    @MotechListener(subjects = TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT)
    public void handleWeeklyAdherenceTrendToOutboxEvent(MotechEvent motechEvent) {
        String externalId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = allPatients.get(externalId);

        if (patient != null && patient.allowAdherenceCalls()) {
            OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
            voiceMessage.setPartyId(externalId);
            voiceMessage.setStatus(OutboundVoiceMessageStatus.PENDING);
            voiceMessage.setExpirationDate(DateUtil.today().plusWeeks(1).toDate());
            VoiceMessageType voiceMessageType = new VoiceMessageType();
            voiceMessageType.setPriority(MessagePriority.MEDIUM);
            voiceMessageType.setVoiceMessageTypeName(OutboxController.VOICE_MESSAGE_COMMAND_AUDIO);
            voiceMessage.setVoiceMessageType(voiceMessageType);
            voiceMessage.setCreationTime(DateUtil.now().toDate());
            Map<String, Object> parameters = new HashMap<String, Object>();
            List<String> commands = Arrays.asList("weeklyAdherenceOutBoxCommand");  // TODO: not used, can probably be removed
            parameters.put(OutboxController.VOICE_MESSAGE_COMMAND, commands);
            voiceMessage.setParameters(parameters);
            outboxService.addMessage(voiceMessage);

            dailyReminderAdherenceTrendService.raiseAdherenceFallingAlert(externalId);
        }
    }
}
