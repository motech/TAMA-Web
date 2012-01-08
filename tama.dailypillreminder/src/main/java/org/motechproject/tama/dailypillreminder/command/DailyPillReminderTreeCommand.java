package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.command.BaseTreeCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

public abstract class DailyPillReminderTreeCommand extends BaseTreeCommand {

    protected DailyPillReminderService dailyPillReminderService;

    protected DailyPillReminderTreeCommand(DailyPillReminderService dailyPillReminderService) {
        super();
        this.dailyPillReminderService = dailyPillReminderService;
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(new DailyPillReminderContext(tamaivrContext, dailyPillReminderService));
    }

    @Override
    public final String[] executeCommand(TAMAIVRContext tamaivrContext) {
        return executeCommand(new DailyPillReminderContext(tamaivrContext, dailyPillReminderService));
    }

    public abstract String[] executeCommand(DailyPillReminderContext context);
}
