package org.motechproject.tamacallflow.service;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdherenceService {

    private DailyReminderAdherenceService dailyReminderAdherenceService;
    private FourDayRecallService fourDayRecallService;

    @Autowired
    public AdherenceService(DailyReminderAdherenceService dailyReminderAdherenceService, FourDayRecallService fourDayRecallService) {
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
        this.fourDayRecallService = fourDayRecallService;
    }

    public boolean isDosageMissedLastWeek(Patient patient) {
        double adherenceForLastWeek = 0;
        if (patient.isOnDailyPillReminder()) {
            adherenceForLastWeek = dailyReminderAdherenceService.getAdherenceForLastWeekInPercentage(patient.getId(), DateUtil.now());
        } else {
            adherenceForLastWeek = fourDayRecallService.getAdherencePercentageForPreviousWeek(patient.getId());
            if (adherenceForLastWeek == 0.0 &&
                    fourDayRecallService.getFirstWeeksFourDayRecallRetryEndDate(patient).isAfter(DateUtil.now()))
                return false;
        }
        return (adherenceForLastWeek != 100.0);
    }

    public boolean anyDoseTakenLateSince(Patient patient, LocalDate since) {
        if (!patient.isOnDailyPillReminder()) return false;
        return dailyReminderAdherenceService.anyDoseTakenLateSince(patient.getId(), since);
    }
}
