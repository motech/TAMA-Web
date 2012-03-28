package org.motechproject.tama.patient.service.registry;

import org.motechproject.tama.patient.service.Outbox;
import org.springframework.stereotype.Component;

@Component
public class OutboxRegistry {

    private Outbox outbox;

    public void registerOutbox(Outbox outbox) {
        this.outbox = outbox;
    }

    public Outbox getOutbox() {
        return outbox;
    }

}
