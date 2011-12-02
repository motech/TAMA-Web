package org.motechproject.tamahealthtip.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tamacommon.ControllerURLs;
import org.motechproject.tamahealthtip.service.HealthTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping(ControllerURLs.HEALTH_TIPS_URL)
public class HealthTipsController extends SafeIVRController {

    public static final String HEALTH_TIP_PLAY_COUNT = "healthtip.playcount";
    private HealthTipService healthTipService;
    private TAMAIVRContextFactory tamaivrContextFactory;

    Properties ivrProperties;

    @Autowired
    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage,
                                KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                @Qualifier("ivrProperties") Properties ivrProperties) {
        this(healthTipService, ivrMessage, callDetailRecordsService, standardResponseController, ivrProperties, new TAMAIVRContextFactory());
    }

    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage,
                                KookooCallDetailRecordsService callDetailRecordsService,
                                StandardResponseController standardResponseController,
                                Properties ivrProperties,
                                TAMAIVRContextFactory tamaivrContextFactory) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.healthTipService = healthTipService;
        this.ivrProperties = ivrProperties;
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
        Integer maxPlayCount = Integer.valueOf((String) ivrProperties.get(HEALTH_TIP_PLAY_COUNT));
        if (playedCount == maxPlayCount) {
            endHealthTipFlow(tamaivrContext);
            return ivrResponseBuilder;
        }

        List<String> playList = healthTipService.getPlayList(patientId);
        if (playList != null && playList.size() > 0) {
            String message = playList.get(0);
            tamaivrContext.setLastPlayedHealthTip(message);
            ivrResponseBuilder.withPlayAudios(message);
            tamaivrContext.setPlayedHealthTipsCount(playedCount + 1);
        } else {
            endHealthTipFlow(tamaivrContext);
        }
        return ivrResponseBuilder;
    }

    private void endHealthTipFlow(TAMAIVRContext tamaivrContext) {
        tamaivrContext.callState(CallState.END_OF_FLOW);
        tamaivrContext.setPlayedHealthTipsCount(0);
    }

}