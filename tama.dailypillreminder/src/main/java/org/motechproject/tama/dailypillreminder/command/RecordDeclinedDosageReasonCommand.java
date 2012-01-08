package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.DeclinedDosageLog;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageNotTakenReason;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordDeclinedDosageReasonCommand extends DailyPillReminderTreeCommand {
    private AllDosageAdherenceLogs logs;

    @Autowired
    public RecordDeclinedDosageReasonCommand(AllDosageAdherenceLogs logs, DailyPillReminderService dailyPillReminderService) {
        super(dailyPillReminderService);
        this.logs = logs;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        DosageNotTakenReason reason = DosageNotTakenReason.from(context.dtmfInput());
        DosageAdherenceLog todayLog = logs.findByDosageIdAndDate(context.currentDose().getDosageId(), DateUtil.today());
        if (todayLog != null) {
            DeclinedDosageLog newLog = new DeclinedDosageLog(todayLog, reason);
            logs.update(newLog.getAdherenceLog());
        }

        return new String[0];
    }
}