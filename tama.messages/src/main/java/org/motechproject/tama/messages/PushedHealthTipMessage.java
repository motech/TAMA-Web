package org.motechproject.tama.messages;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushedHealthTipMessage {

    private HealthTipService healthTipService;

    @Autowired
    public PushedHealthTipMessage(HealthTipService healthTipService) {
        this.healthTipService = healthTipService;
    }

    public boolean addToResponse(KookooIVRResponseBuilder ivrResponseBuilder, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        String healthTip = healthTipService.nextHealthTip(tamaivrContext.patientDocumentId());
        ivrResponseBuilder.withPlayAudios(healthTip);
        return true;
    }

    public void markAsRead(String patientDocumentId, String lastPlayedHealthTip) {
        healthTipService.markAsPlayed(patientDocumentId, lastPlayedHealthTip);
    }
}
