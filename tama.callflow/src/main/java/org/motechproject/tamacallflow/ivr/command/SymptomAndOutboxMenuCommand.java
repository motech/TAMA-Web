package org.motechproject.tamacallflow.ivr.command;

import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SymptomAndOutboxMenuCommand extends BaseTreeCommand {
    private VoiceOutboxService voiceOutboxService;

    @Autowired
    public SymptomAndOutboxMenuCommand(VoiceOutboxService voiceOutboxService, PillReminderService pillReminderService) {
        super(pillReminderService);
        this.voiceOutboxService = voiceOutboxService;
    }

    @Override
    public String[] executeCommand(TAMAIVRContext ivrContext) {
        List<String> menuOptions = new ArrayList<String>();
        menuOptions.add(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION);
        if (voiceOutboxService.getNumberPendingMessages(ivrContext.patientId()) != 0) {
            menuOptions.add(TamaIVRMessage.OUTBOX_MENU_OPTION);
        }
        return menuOptions.toArray(new String[menuOptions.size()]);
    }
}
