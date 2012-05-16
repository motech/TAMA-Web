package org.motechproject.tama.outbox.context;

import org.motechproject.ivr.kookoo.IvrContext;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import javax.servlet.http.HttpServletRequest;

// This class is created instead of using TAMAIVRContext because we might want to move Outbox IVR to platform
public class OutboxContext {
    private static final String LAST_PLAYED_VOICE_MESSAGE_ID = "LastPlayedVoiceMessageID";
    private static final String OUTBOX_COMPLETED = "outboxCompleted";
    private IvrContext kooKooIVRContext;
    private HttpServletRequest request;

    protected OutboxContext() {
    }

    public OutboxContext(IvrContext kooKooIVRContext) {
        this.kooKooIVRContext = kooKooIVRContext;
        this.request = kooKooIVRContext.httpRequest();
        kooKooIVRContext.addToCallSession(OUTBOX_COMPLETED, Boolean.toString(false));
    }

    public String partyId() {
        return (String) request.getSession().getAttribute(TAMAIVRContext.PATIENT_ID);
    }

    public String lastPlayedMessageId() {
        return kooKooIVRContext.getFromCallSession(LAST_PLAYED_VOICE_MESSAGE_ID);
    }

    public void lastPlayedMessageId(String messageId) {
        kooKooIVRContext.addToCallSession(LAST_PLAYED_VOICE_MESSAGE_ID, messageId);
    }

    public String preferredLanguage() {
        return kooKooIVRContext.preferredLanguage();
    }

    public String callId() {
        return kooKooIVRContext.callId();
    }

    public boolean hasOutboxCompleted() {
        return kooKooIVRContext.<Boolean>getFromCallSession(OUTBOX_COMPLETED);
    }

    public void outboxCompleted() {
        kooKooIVRContext.addToCallSession(OUTBOX_COMPLETED, Boolean.toString(true));
    }
}
