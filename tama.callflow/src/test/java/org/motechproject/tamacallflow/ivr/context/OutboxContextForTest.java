package org.motechproject.tamacallflow.ivr.context;

public class OutboxContextForTest extends OutboxContext {
    private String partyId;
    private String lastPlayedMessageId;
    private String preferredLanguage;
    private String callId;
    private boolean outboxCompleted;

    @Override
    public String partyId() {
        return partyId;
    }

    @Override
    public String lastPlayedMessageId() {
        return lastPlayedMessageId;
    }

    @Override
    public void lastPlayedMessageId(String messageId) {
        this.lastPlayedMessageId = messageId;
    }

    @Override
    public String preferredLanguage() {
        return preferredLanguage;
    }

    public OutboxContextForTest preferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        return this;
    }

    public OutboxContextForTest partyId(String partyId) {
        this.partyId = partyId;
        return this;
    }

    @Override
    public String callId() {
        return callId;
    }

    public OutboxContextForTest callId(String callId) {
        this.callId = callId;
        return this;
    }

    @Override
    public boolean hasOutboxCompleted() {
        return outboxCompleted;
    }

    @Override
    public void outboxCompleted() {
        outboxCompleted = true;
    }
}