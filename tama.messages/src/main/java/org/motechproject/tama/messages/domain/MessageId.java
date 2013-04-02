package org.motechproject.tama.messages.domain;

public class MessageId {

    private String messageType;
    private String id;

    public MessageId(String messageType, String id) {
        this.messageType = messageType;
        this.id = id;
    }

    public String getMessageId() {
        return messageType + id;
    }
}
