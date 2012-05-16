package org.motechproject.tama.healthtips.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.domain.IVRMessage;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.healthtips.domain.HealthTipsProperties;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ControllerURLs.HEALTH_TIPS_URL)
public class HealthTipsController extends SafeIVRController {

    private HealthTipService healthTipService;
    
    private TAMAIVRContextFactory tamaivrContextFactory;

    private HealthTipsProperties healthTipsProperties;

    @Autowired
    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage,
                                KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                HealthTipsProperties healthTipsProperties) {
        this(healthTipService, ivrMessage, callDetailRecordsService, standardResponseController, healthTipsProperties, new TAMAIVRContextFactory());
    }

    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage,
                                KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                HealthTipsProperties healthTipsProperties,
                                TAMAIVRContextFactory tamaivrContextFactory) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.healthTipService = healthTipService;
        this.healthTipsProperties = healthTipsProperties;
        this.tamaivrContextFactory = tamaivrContextFactory;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = tamaivrContextFactory.create(kooKooIVRContext);
        String patientId = kooKooIVRContext.externalId();

        KookooIVRResponseBuilder ivrResponseBuilder = KookooResponseFactory.empty(kooKooIVRContext.callId()).language(kooKooIVRContext.preferredLanguage());

        String lastPlayedMessage = tamaivrContext.getLastPlayedHealthTip();
        if (lastPlayedMessage != null) healthTipService.markAsPlayed(patientId, lastPlayedMessage);
        int playedCount = tamaivrContext.getPlayedHealthTipsCount();
        Integer maxPlayCount = healthTipsProperties.getHealthTipPlayCount();
        if (playedCount == maxPlayCount) {
            endHealthTipFlow(tamaivrContext);
            return ivrResponseBuilder;
        }

        String healthTip = healthTipService.nextHealthTip(patientId);
        if (!StringUtils.isEmpty(healthTip)) {
            tamaivrContext.setLastPlayedHealthTip(healthTip);
            ivrResponseBuilder.withPlayAudios(healthTip);
            tamaivrContext.setPlayedHealthTipsCount(playedCount + 1);
        } else {
            ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NO_HEALTHTIP_MESSAGES);
            endHealthTipFlow(tamaivrContext);
        }
        return ivrResponseBuilder;
    }

    private void endHealthTipFlow(TAMAIVRContext tamaivrContext) {
        tamaivrContext.callState(CallState.END_OF_FLOW);
        tamaivrContext.setPlayedHealthTipsCount(0);
    }
}