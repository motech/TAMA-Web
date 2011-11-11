package org.motechproject.tama.ivr.factory;

import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.tama.web.command.callforwarding.DialStateCommand;

public class DialPromptFactory {
    public static DialPrompt get() {
        final DialPrompt dialPrompt = new DialPrompt();
        dialPrompt.setCommand(new DialStateCommand());
        return dialPrompt;
    }
}
