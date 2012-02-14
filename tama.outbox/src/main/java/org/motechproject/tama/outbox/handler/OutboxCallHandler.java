package org.motechproject.tama.outbox.handler;

import org.motechproject.model.MotechEvent;

public interface OutboxCallHandler {
    public void handle(MotechEvent motechEvent);
}
