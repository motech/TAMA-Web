package org.motechproject.tama.ivr;

import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.motechproject.server.service.ivr.PostTreeCallContinuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallContinuationAfterTree implements PostTreeCallContinuation {
    private IVRMessage ivrMessage;
    private VoiceOutboxService voiceOutboxService;

    @Autowired
    public CallContinuationAfterTree(IVRMessage ivrMessage, VoiceOutboxService voiceOutboxService) {
        this.ivrMessage = ivrMessage;
        this.voiceOutboxService = voiceOutboxService;
    }

    @Override
    public void continueCall(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
        final int pendingMessagesCount = voiceOutboxService.getNumberPendingMessages(ivrContext.ivrSession().getExternalId());
        if(pendingMessagesCount != 0) {
            ivrResponseBuilder.withNextUrl(ivrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL));
        } else {
            ivrResponseBuilder.withPlayAudios(ivrMessage.getSignatureMusic());
            ivrResponseBuilder.withHangUp();
        }
    }
}