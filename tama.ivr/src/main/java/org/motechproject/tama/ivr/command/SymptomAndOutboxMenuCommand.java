package org.motechproject.tama.ivr.command;

import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.OutboxModuleStratergy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SymptomAndOutboxMenuCommand extends BaseTreeCommand {
    private OutboxModuleStratergy outboxModuleStratergy;

    public void registerOutboxModule(OutboxModuleStratergy outboxModuleStratergy) {
        this.outboxModuleStratergy = outboxModuleStratergy;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> menuOptions = new ArrayList<String>();
        menuOptions.add(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
        if (outboxModuleStratergy.getNumberPendingMessages(ivrContext.patientId()) != 0) {
            menuOptions.add(TamaIVRMessage.OUTBOX_MENU_OPTION);
        }
        return menuOptions.toArray(new String[menuOptions.size()]);
    }
}
