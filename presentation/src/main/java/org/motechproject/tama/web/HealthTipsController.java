package org.motechproject.tama.web;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.motechproject.tama.service.HealthTipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(TAMACallFlowController.HEALTH_TIPS_URL)
public class HealthTipsController extends SafeIVRController {

    private HealthTipService healthTipService;
    static final String LAST_PLAYED_HEALTH_TIP = "lastPlayedHealthTip";
    static final String HEALTH_TIPS_PLAYED_COUNT = "healthTipsPlayedCount";

    @Autowired
    public HealthTipsController(HealthTipService healthTipService, IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService, StandardResponseController standardResponseController) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.healthTipService = healthTipService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        String patientId = kooKooIVRContext.externalId();
        KookooIVRResponseBuilder ivrResponseBuilder = KookooResponseFactory.empty(kooKooIVRContext.callId()).language(kooKooIVRContext.preferredLanguage());

        healthTipService.markAsPlayed(patientId, getLastPlayedMessage(kooKooIVRContext));

        List<String> playList = healthTipService.getPlayList(patientId);
        if (playList != null && playList.size() > 0) {
            String message = playList.get(0);
            setLastPlayedMessage(kooKooIVRContext, message);
            ivrResponseBuilder.withPlayAudios(message);
            setPlayedCount(kooKooIVRContext, getPlayedCount(kooKooIVRContext) + 1);
        }
        return ivrResponseBuilder;
    }

    private void setPlayedCount(KooKooIVRContext kooKooIVRContext, int count) {
        kooKooIVRContext.cookies().add(HEALTH_TIPS_PLAYED_COUNT, String.valueOf(count));
    }

    private int getPlayedCount(KooKooIVRContext kooKooIVRContext) {
        String value = kooKooIVRContext.cookies().getValue(HEALTH_TIPS_PLAYED_COUNT);
        return value==null?0:Integer.valueOf(value);
    }

    private void setLastPlayedMessage(KooKooIVRContext kooKooIVRContext, String message) {
        kooKooIVRContext.cookies().add(LAST_PLAYED_HEALTH_TIP, message);
    }

    private String getLastPlayedMessage(KooKooIVRContext kooKooIVRContext) {
        return kooKooIVRContext.cookies().getValue(LAST_PLAYED_HEALTH_TIP);
    }

}