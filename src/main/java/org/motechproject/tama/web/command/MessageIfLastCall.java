package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.springframework.stereotype.Component;

@Component
public class MessageIfLastCall implements ITreeCommand{
    @Override
    public String[] execute(Object o) {
        return new String[0];
    }
}
