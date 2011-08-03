package org.motechproject.tama.ivr.builder;

import java.util.ArrayList;
import java.util.List;

public class IVRDayMessageBuilder {

    private String currentDosageId;
    private String previousDosageId;
    private int previousDosageStartHour;

    public IVRDayMessageBuilder(String currentDosageId, String previousDosageId, int previousDosageStartHour) {
        this.currentDosageId = currentDosageId;
        this.previousDosageId = previousDosageId;
        this.previousDosageStartHour = previousDosageStartHour;
    }

    public List<String> getMessages(String yesterday, String morning, String evening) {
        List<String> messages = new ArrayList<String>();
        if (currentDosageId.equals(previousDosageId) || previousDosageStartHour > 12)
            messages.add(yesterday);
        if (previousDosageStartHour < 12)
            messages.add(morning);
        else
            messages.add(evening);
        return messages;
    }
}
