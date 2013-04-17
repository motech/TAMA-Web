package org.motechproject.tama.dailypillreminder.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.api.EventKeys;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdherenceTrendListener {
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private AllPatients allPatients;

    @Autowired
    public AdherenceTrendListener(DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, AllPatients allPatients) {
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.allPatients = allPatients;
    }

    @MotechListener(subjects = TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT)
    public void handleAdherenceTrendEvent(MotechEvent motechEvent) {
        String externalId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = allPatients.get(externalId);

        if (patient != null && patient.allowAdherenceCalls()) {
            dailyReminderAdherenceTrendService.raiseAlertIfAdherenceTrendIsFalling(externalId, DateUtil.now());
        }
    }
}
