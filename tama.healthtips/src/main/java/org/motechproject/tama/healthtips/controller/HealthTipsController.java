package org.motechproject.tama.healthtips.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.healthtips.criteria.ContinueToHealthTipsCriteria;
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
    private ContinueToHealthTipsCriteria healthTipsCriteria;

    private HealthTipsProperties healthTipsProperties;

    @Autowired
    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage,
                                KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                HealthTipsProperties healthTipsProperties,
                                ContinueToHealthTipsCriteria healthTipsCriteria
    ) {
        this(healthTipService, ivrMessage, callDetailRecordsService, standardResponseController, healthTipsProperties, new TAMAIVRContextFactory(), healthTipsCriteria);
    }

    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage,
                                KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                HealthTipsProperties healthTipsProperties,
                                TAMAIVRContextFactory tamaivrContextFactory,
                                ContinueToHealthTipsCriteria healthTipsCriteria
    ) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.healthTipService = healthTipService;
        this.healthTipsProperties = healthTipsProperties;
        this.tamaivrContextFactory = tamaivrContextFactory;
        this.healthTipsCriteria = healthTipsCriteria;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        String patientId = kooKooIVRContext.externalId();
        TAMAIVRContext tamaivrContext = tamaivrContextFactory.create(kooKooIVRContext);
        KookooIVRResponseBuilder ivrResponseBuilder = createResponseBuilder(kooKooIVRContext);
        int playedCount = tamaivrContext.getPlayedHealthTipsCount();

        if (healthTipsCriteria.shouldContinue(patientId)) {
            markLastPlayedHealthTipAsRead(patientId, tamaivrContext);
            if (exhaustedNumberOfHealthTipsPerCall(tamaivrContext)) {
                endHealthTipFlow(tamaivrContext);
                return ivrResponseBuilder;
            }
            String healthTip = healthTipService.nextHealthTip(patientId);
            if (!StringUtils.isEmpty(healthTip)) {
                playNextHealthTip(tamaivrContext, ivrResponseBuilder, playedCount, healthTip);
            } else {
                noMoreHealthTipsToPlay(tamaivrContext, ivrResponseBuilder);
            }
            return ivrResponseBuilder;
        } else {
            endHealthTipFlow(tamaivrContext);
            return ivrResponseBuilder;
        }
    }

    private void playNextHealthTip(TAMAIVRContext tamaivrContext, KookooIVRResponseBuilder ivrResponseBuilder, int playedCount, String healthTip) {
        tamaivrContext.setLastPlayedHealthTip(healthTip);
        ivrResponseBuilder.withPlayAudios(healthTip);
        tamaivrContext.setPlayedHealthTipsCount(playedCount + 1);
    }

    private void noMoreHealthTipsToPlay(TAMAIVRContext tamaivrContext, KookooIVRResponseBuilder ivrResponseBuilder) {
        ivrResponseBuilder.withPlayAudios(TamaIVRMessage.NO_HEALTHTIP_MESSAGES);
        endHealthTipFlow(tamaivrContext);
    }

    private void markLastPlayedHealthTipAsRead(String patientId, TAMAIVRContext tamaivrContext) {
        String lastPlayedMessage = tamaivrContext.getLastPlayedHealthTip();
        if (lastPlayedMessage != null) healthTipService.markAsPlayed(patientId, lastPlayedMessage);
    }

    private boolean exhaustedNumberOfHealthTipsPerCall(TAMAIVRContext context) {
        int playedCount = context.getPlayedHealthTipsCount();
        Integer maxPlayCount = healthTipsProperties.getHealthTipPlayCount();
        return playedCount == maxPlayCount;
    }

    private KookooIVRResponseBuilder createResponseBuilder(KooKooIVRContext kooKooIVRContext) {
        return KookooResponseFactory.empty(kooKooIVRContext.callId()).language(kooKooIVRContext.preferredLanguage());
    }

    private void endHealthTipFlow(TAMAIVRContext tamaivrContext) {
        tamaivrContext.callState(CallState.END_OF_HEALTH_TIPS_FLOW);
        tamaivrContext.setPlayedHealthTipsCount(0);
    }
}