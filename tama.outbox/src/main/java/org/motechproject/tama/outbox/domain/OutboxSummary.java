package org.motechproject.tama.outbox.domain;

public class OutboxSummary {

    private String messageId;
    private String createdOn;
    private String typeName;
    private String playedOn;
    private String playedFiles;

    public String getMessageId() {
        return messageId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getPlayedOn() {
        return playedOn;
    }

    public String getPlayedFiles() {
        return playedFiles;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setPlayedOn(String playedOn) {
        this.playedOn = playedOn;
    }

    public void setPlayedFiles(String playedFiles) {
        this.playedFiles = playedFiles;
    }
}
