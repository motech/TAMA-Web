package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;

public abstract class BaseTreeCommand implements ITreeCommand {
    protected PillReminderService pillReminderService;

    protected BaseTreeCommand(PillReminderService pillReminderService) {
        this.pillReminderService = pillReminderService;
    }

    protected PillRegimenSnapshot pillRegimenSnapshot(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        return pillRegimenSnapshot(tamaivrContext);
    }

    protected PillRegimenSnapshot pillRegimenSnapshot(TAMAIVRContext tamaivrContext) {
        PillRegimenResponse pillRegimenResponse = pillRegimen(tamaivrContext);
        return new PillRegimenSnapshot(tamaivrContext, pillRegimenResponse);
    }

    protected PillRegimenResponse pillRegimen(KooKooIVRContext kooKooIVRContext) {
        return pillRegimen(new TAMAIVRContext(kooKooIVRContext));
    }

    protected PillRegimenResponse pillRegimen(TAMAIVRContext tamaivrContext) {
        return tamaivrContext.pillRegimen(pillReminderService);
    }

    @Override
    public String[] execute(Object o) {
        KooKooIVRContext ivrContext = (KooKooIVRContext) o;
        return executeCommand(new TAMAIVRContext(ivrContext));
    }

    public abstract String[] executeCommand(TAMAIVRContext tamaivrContext);
}