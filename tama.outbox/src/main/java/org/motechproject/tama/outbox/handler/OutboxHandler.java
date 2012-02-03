package org.motechproject.tama.outbox.handler;

import org.motechproject.model.MotechEvent;

public interface OutboxHandler {
    public void handle(MotechEvent motechEvent);
}
