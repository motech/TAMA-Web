package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
    // TODO: this class simply wraps abstract DosageAdherenceCommand. can use DosageAdherenceCommand, by making it non-abstract
public class MessageForAdherenceWhenPreviousDosageNotCapturedCommand extends DosageAdherenceCommand {
    @Autowired
    public MessageForAdherenceWhenPreviousDosageNotCapturedCommand(AllDosageAdherenceLogs allDosageAdherenceLogs, TamaIVRMessage ivrMessage, PillReminderService pillReminderService) {
        super(allDosageAdherenceLogs, ivrMessage, pillReminderService);
    }

    @Override
    public String[] executeCommand(TAMAIVRContext tamaivrContext) {
        return getAdherenceMessage(tamaivrContext);
    }
}