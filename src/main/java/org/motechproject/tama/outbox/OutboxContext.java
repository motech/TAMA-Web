package org.motechproject.tama.outbox;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.util.Cookies;

public class OutboxContext {
    private Cookies cookies;

    private static final String LAST_PLAYED_VOICE_MESSAGE_ID = "LastPlayedVoiceMessageID";
    private KooKooIVRContext kooKooIVRContext;

    protected OutboxContext() {
    }

    public OutboxContext(KooKooIVRContext kooKooIVRContext) {
        this.kooKooIVRContext = kooKooIVRContext;
        this.cookies = kooKooIVRContext.cookies();
    }

    public String partyId() {
        return cookies.getValue(TAMAIVRContext.PATIENT_ID);
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
}
