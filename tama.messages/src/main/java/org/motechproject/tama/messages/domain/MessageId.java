package org.motechproject.tama.messages.domain;

public class MessageId {

    private Method method;
    private String messageType;
    private String id;

    public MessageId(Method method, String messageType, String id) {
        this.method = method;
        this.messageType = messageType;
        this.id = id;
    }

    public String getMessageId() {
        return method.name() + messageType + id;
    }
}
