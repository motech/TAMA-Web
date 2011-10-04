package org.motechproject.tama.web.command.fourdayrecall;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllDosagesTaken implements ITreeCommand {
    @Override
    public String[] execute(Object o) {
        List<String> messages = new ArrayList<String>();
        messages.add(TamaIVRMessage.FDR_ALL_DOSAGES_TAKEN);
        return messages.toArray(new String[messages.size()]);
    }
}