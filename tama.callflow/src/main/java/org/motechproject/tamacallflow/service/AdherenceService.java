package org.motechproject.tamacallflow.service;

import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdherenceService {


    DailyReminderAdherenceService dailyReminderAdherenceService;
    FourDayRecallService fourDayRecallService;

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
           if( adherenceForLastWeek == 0.0 &&
               fourDayRecallService.getFirstWeeksFourDayRecallRetryEndDate(patient).isAfter(DateUtil.now()))
                  return false;
       }
       return (adherenceForLastWeek != 100.0);
    }


}
