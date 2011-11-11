package org.motechproject.tama.outbox;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;

// This class is created instead of using TAMAIVRContext because we might want to move Outbox IVR to platform
public class OutboxContext {
    private Cookies cookies;

    private static final String LAST_PLAYED_VOICE_MESSAGE_ID = "LastPlayedVoiceMessageID";
    private static final String OUTBOX_COMPLETED = "outboxCompleted";
    private KooKooIVRContext kooKooIVRContext;
    private HttpServletRequest request;

    protected OutboxContext() {
    }

    public OutboxContext(KooKooIVRContext kooKooIVRContext) {
        this.kooKooIVRContext = kooKooIVRContext;
        this.cookies = kooKooIVRContext.cookies();
        this.request = kooKooIVRContext.httpRequest();
        cookies.add(OUTBOX_COMPLETED, Boolean.toString(false));
    }

    public String partyId() {
        return (String) request.getSession().getAttribute(TAMAIVRContext.PATIENT_ID);
    }

    public String lastPlayedMessageId() {
        return cookies.getValue(LAST_PLAYED_VOICE_MESSAGE_ID);
    }

    public void lastPlayedMessageId(String messageId) {
        cookies.add(LAST_PLAYED_VOICE_MESSAGE_ID, messageId);
    }

    public String preferredLanguage() {
        return kooKooIVRContext.preferredLanguage();
    }

    public String callId() {
        return kooKooIVRContext.callId();
    }

    public boolean hasOutboxCompleted() {
        return Boolean.parseBoolean(cookies.getValue(OUTBOX_COMPLETED));
    }

    public void outboxCompleted() {
        cookies.add(OUTBOX_COMPLETED, Boolean.toString(true));
    }
}
