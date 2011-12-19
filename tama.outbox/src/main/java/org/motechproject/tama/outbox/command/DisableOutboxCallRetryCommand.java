package org.motechproject.tama.outbox.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.outbox.service.OutboxSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DisableOutboxCallRetryCommand implements ITreeCommand {

    @Autowired
    OutboxSchedulerService outboxSchedulerService;

    @Override
    public String[] execute(Object obj) {
        if (obj instanceof KooKooIVRContext) {
            KooKooIVRContext ctx = (KooKooIVRContext) obj;
            outboxSchedulerService.unscheduleRepeatingJobForOutboxCall(ctx.externalId());
        }
        return new String[0];
    }
}
