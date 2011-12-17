package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DisableOutboxCallRetryCommand implements ITreeCommand {

    @Autowired
    TamaSchedulerService tamaSchedulerService;

    @Override
    public String[] execute(Object obj) {
        if (obj instanceof KooKooIVRContext) {
            KooKooIVRContext ctx = (KooKooIVRContext) obj;
            tamaSchedulerService.unscheduleRepeatingJobForOutboxCall(ctx.externalId());
        }
        return new String[0];
    }
}
