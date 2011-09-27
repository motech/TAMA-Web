package org.motechproject.tama.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.TAMAConstants;

public class AdherenceTrendListener {
	@MotechListener(subjects = TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT)
	public void handleWeeklyAdherenceTrendToOutboxEvent(MotechEvent motechEvent) {

		// outboxAPI.post("weeklyAdherence", motechEvent.getParameters());
	}

}
