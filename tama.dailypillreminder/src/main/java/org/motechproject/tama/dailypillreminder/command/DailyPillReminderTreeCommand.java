package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimenSnapshot;
import org.motechproject.tama.ivr.command.BaseTreeCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

public abstract class DailyPillReminderTreeCommand extends BaseTreeCommand {

    protected PillReminderService pillReminderService;

    protected DailyPillReminderTreeCommand(PillReminderService pillReminderService) {
        super();
        this.pillReminderService = pillReminderService;
    }

    protected PillRegimenSnapshot pillRegimenSnapshot(DailyPillReminderContext context) {
        PillRegimenResponse pillRegimenResponse = pillRegimenResponse(context);
        return new PillRegimenSnapshot(context, pillRegimenResponse);
    }

    protected PillRegimenResponse pillRegimen(DailyPillReminderContext context) {
        return pillRegimenResponse(context);
    }

    protected PillRegimenResponse pillRegimenResponse(DailyPillReminderContext context) {
        return context.pillRegimen(pillReminderService);
    }

    @Override
    public String[] execute(Object o) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContext((KooKooIVRContext) o);
        return executeCommand(new DailyPillReminderContext(tamaivrContext));
    }

    @Override
    public final String[] executeCommand(TAMAIVRContext tamaivrContext) {
        return executeCommand(new DailyPillReminderContext(tamaivrContext));
    }

    public abstract String[] executeCommand(DailyPillReminderContext context);
}
