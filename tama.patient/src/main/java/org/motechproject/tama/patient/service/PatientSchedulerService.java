package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.scheduler.DailyPillReminderScheduler;
import org.motechproject.tama.patient.scheduler.FourDayRecallScheduler;
import org.motechproject.tama.patient.scheduler.OutboxScheduler;
import org.springframework.stereotype.Component;

@Component
public class PatientSchedulerService {

    private DailyPillReminderScheduler dailyPillReminderScheduler;
    private FourDayRecallScheduler fourDayRecallScheduler;
    private OutboxScheduler outboxScheduler;

    public void registerDailyPillReminderScheduler(DailyPillReminderScheduler dailyPillReminderScheduler) {
        this.dailyPillReminderScheduler = dailyPillReminderScheduler;
    }

    public void registerFourDayRecallScheduler(FourDayRecallScheduler fourDayRecallScheduler) {
        this.fourDayRecallScheduler = fourDayRecallScheduler;
    }

    public void registerOutboxScheduler(OutboxScheduler outboxScheduler) {
        this.outboxScheduler = outboxScheduler;
    }
}