package org.motechproject.tama.symptomreporting.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;

public class EndOfCallListener {
    @MotechListener(subjects = "close_call")
    public void handle(MotechEvent event) {

    }
}