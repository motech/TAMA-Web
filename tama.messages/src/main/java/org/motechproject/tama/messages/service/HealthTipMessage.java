package org.motechproject.tama.messages.service;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.healthtips.criteria.ContinueToHealthTipsCriteria;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
public class HealthTipMessage {

    private HealthTipService healthTipService;
    private ContinueToHealthTipsCriteria continueToHealthTipsCriteria;

    @Autowired
    public HealthTipMessage(HealthTipService healthTipService, ContinueToHealthTipsCriteria continueToHealthTipsCriteria) {
        this.healthTipService = healthTipService;
        this.continueToHealthTipsCriteria = continueToHealthTipsCriteria;
    }

    public boolean hasAnyMessage(KooKooIVRContext kooKooIVRContext, TAMAMessageType type) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (continueToHealthTipsCriteria.shouldContinue(tamaivrContext.patientDocumentId())) {
            String healthTip = this.healthTipService.nextHealthTip(tamaivrContext.patientDocumentId(), type);
            return isNotBlank(healthTip);
        } else {
            return false;
        }
    }

    public KookooIVRResponseBuilder getResponse(KooKooIVRContext kooKooIVRContext, TAMAMessageType type) {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(kooKooIVRContext.callId());
        addToResponse(response, kooKooIVRContext, type);
        return response;
    }

    public void markAsRead(String patientDocumentId, String lastPlayedHealthTip) {
        if (isNotBlank(lastPlayedHealthTip)) {
            healthTipService.markAsPlayed(patientDocumentId, lastPlayedHealthTip);
        }
    }

    public boolean addToResponse(KookooIVRResponseBuilder ivrResponseBuilder, KooKooIVRContext kooKooIVRContext, TAMAMessageType type) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (continueToHealthTipsCriteria.shouldContinue(tamaivrContext.patientDocumentId())) {
            String healthTip = healthTipService.nextHealthTip(tamaivrContext.patientDocumentId(), type);
            tamaivrContext.setLastPlayedHealthTip(healthTip);
            ivrResponseBuilder.withPlayAudios(healthTip);
            return true;
        } else {
            return false;
        }
    }
}
