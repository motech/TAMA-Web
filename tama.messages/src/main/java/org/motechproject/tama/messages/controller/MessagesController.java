package org.motechproject.tama.messages.controller;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.SafeIVRController;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.common.ControllerURLs;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.messages.PushedHealthTipMessage;
import org.motechproject.tama.messages.PushedOutboxMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
@RequestMapping(ControllerURLs.PUSH_MESSAGES_URL)
public class MessagesController extends SafeIVRController {

    private PushedOutboxMessage pushedOutboxMessage;
    private PushedHealthTipMessage pushedHealthTipMessage;
    private PatientService patientService;

    @Autowired
    public MessagesController(IVRMessage ivrMessage, KookooCallDetailRecordsService callDetailRecordsService,
                              StandardResponseController standardResponseController,
                              PushedOutboxMessage pushedOutboxMessage, PushedHealthTipMessage pushedHealthTipMessage,
                              PatientService patientService) {
        super(ivrMessage, callDetailRecordsService, standardResponseController);
        this.pushedOutboxMessage = pushedOutboxMessage;
        this.pushedHealthTipMessage = pushedHealthTipMessage;
        this.patientService = patientService;
    }

    @Override
    public KookooIVRResponseBuilder gotDTMF(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withSid(tamaivrContext.callId());
        if (markMessageLastAsRead(kooKooIVRContext)) {
            tamaivrContext.callState(CallState.PUSH_MESSAGES_COMPLETE);
            return response;
        } else {
            return playNextMessage(kooKooIVRContext, response);
        }
    }

    private KookooIVRResponseBuilder playNextMessage(KooKooIVRContext kooKooIVRContext, KookooIVRResponseBuilder response) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (patientService.getPatientReport(tamaivrContext.patientDocumentId()).getPatient().isOnWeeklyPillReminder()) {
            response.withPlayAudios(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY);
        }
        return playOutboxMessagesAndHealthTips(kooKooIVRContext, response, tamaivrContext);
    }

    private KookooIVRResponseBuilder playOutboxMessagesAndHealthTips(KooKooIVRContext kooKooIVRContext, KookooIVRResponseBuilder response, TAMAIVRContext tamaivrContext) {
        if (pushedOutboxMessage.addToResponse(response, kooKooIVRContext)) {
            return response;
        } else if (pushedHealthTipMessage.addToResponse(response, kooKooIVRContext)) {
            return response;
        } else {
            tamaivrContext.callState(CallState.PUSH_MESSAGES_COMPLETE);
            return response;
        }
    }

    private boolean markMessageLastAsRead(KooKooIVRContext kooKooIVRContext) {
        TAMAIVRContext tamaivrContext = new TAMAIVRContextFactory().create(kooKooIVRContext);
        if (isNotBlank(new OutboxContext(kooKooIVRContext).lastPlayedMessageId())) {
            pushedOutboxMessage.markLastPlayedMessageAsRead(kooKooIVRContext, tamaivrContext);
            return true;
        } else if (isNotBlank(tamaivrContext.getLastPlayedHealthTip())) {
            pushedHealthTipMessage.markAsRead(tamaivrContext.patientDocumentId(), tamaivrContext.getLastPlayedHealthTip());
            return true;
        }
        return false;
    }
}
