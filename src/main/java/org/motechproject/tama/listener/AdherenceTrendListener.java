package org.motechproject.tama.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdherenceTrendListener {
	private static final String VOICE_MESSAGE_COMMAND_AUDIO = "AudioCommand";
	
	@Autowired
	VoiceOutboxService outboxService;

	@MotechListener(subjects = TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT)
	public void handleWeeklyAdherenceTrendToOutboxEvent(MotechEvent motechEvent) {
		OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
		
		String externalId = (String)motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
		voiceMessage.setPartyId(externalId);
		voiceMessage.setStatus(OutboundVoiceMessageStatus.PENDING);
		voiceMessage.setExpirationDate(DateUtil.today().plusWeeks(1).toDate());
		VoiceMessageType voiceMessageType = new VoiceMessageType();
		voiceMessageType.setPriority(MessagePriority.MEDIUM);
		voiceMessageType.setVoiceMessageTypeName(VOICE_MESSAGE_COMMAND_AUDIO);
		voiceMessage.setVoiceMessageType(voiceMessageType);
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<String> commands = Arrays.asList("weeklyAdherenceOutBoxCommand");
		parameters.put("command", commands );
		voiceMessage.setParameters(parameters );
		outboxService.addMessage(voiceMessage);
	}

}
