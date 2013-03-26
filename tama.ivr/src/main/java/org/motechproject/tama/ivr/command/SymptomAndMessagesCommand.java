package org.motechproject.tama.ivr.command;

import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SymptomAndMessagesCommand extends BaseTreeCommand {

    public void registerOutboxModule() {
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
        menuOptions.add(TamaIVRMessage.OUTBOX_MENU_OPTION);
        return menuOptions.toArray(new String[menuOptions.size()]);
    }
}
