package org.motechproject.tama.messages;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.healthtips.criteria.ContinueToHealthTipsCriteria;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushedHealthTipMessage {

    private HealthTipService healthTipService;
    private ContinueToHealthTipsCriteria continueToHealthTipsCriteria;

    @Autowired
    public PushedHealthTipMessage(HealthTipService healthTipService, ContinueToHealthTipsCriteria continueToHealthTipsCriteria) {
        this.healthTipService = healthTipService;
        this.continueToHealthTipsCriteria = continueToHealthTipsCriteria;
    }

    public boolean addToResponse(KookooIVRResponseBuilder ivrResponseBuilder, KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (continueToHealthTipsCriteria.shouldContinue(tamaivrContext.patientDocumentId())) {
            String healthTip = healthTipService.nextHealthTip(tamaivrContext.patientDocumentId());
            tamaivrContext.setLastPlayedHealthTip(healthTip);
            ivrResponseBuilder.withPlayAudios(healthTip);
            return true;
        } else {
            return false;
        }
    }

    public void markAsRead(String patientDocumentId, String lastPlayedHealthTip) {
        healthTipService.markAsPlayed(patientDocumentId, lastPlayedHealthTip);
    }
}
