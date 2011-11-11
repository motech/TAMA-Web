package org.motechproject.tama.ivr.factory;

import org.junit.Test;
import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.tama.web.command.callforwarding.DialStateCommand;

import static junit.framework.Assert.assertEquals;

public class DialPromptFactoryTest {

    @Test
    public void shouldCreateADialPrompt() {
        DialPrompt dialPrompt = DialPromptFactory.get();
        assertEquals(dialPrompt.getCommand().getClass(), DialStateCommand.class);
    }
}
