package org.motechproject.tama.ivr.command;

import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SymptomAndOutboxMenuCommand extends BaseTreeCommand {
    private OutboxModuleStrategy outboxModuleStrategy;

    public void registerOutboxModule(OutboxModuleStrategy outboxModuleStrategy) {
        this.outboxModuleStrategy = outboxModuleStrategy;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> menuOptions = new ArrayList<String>();
        menuOptions.add(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
        if (outboxModuleStrategy.hasPendingOutboxMessages(ivrContext.patientId())) {
            menuOptions.add(TamaIVRMessage.OUTBOX_MENU_OPTION);
        }
        return menuOptions.toArray(new String[menuOptions.size()]);
    }
}
