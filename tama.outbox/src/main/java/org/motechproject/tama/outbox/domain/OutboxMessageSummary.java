package org.motechproject.tama.outbox.domain;

import org.joda.time.DateTime;

import static org.motechproject.tama.common.TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT;

public class OutboxMessageSummary {

    private String createdOn;
    private String typeName;
    private String playedOn;
    private String playedFiles;

    public OutboxMessageSummary() {
    }

    public OutboxMessageSummary(DateTime createdOn, String typeName) {
        this.createdOn = createdOn == null ? "" : createdOn.toLocalDate().toString();
        this.typeName = typeName;
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

    public void playedOn(DateTime playedOn, String playedFiles) {
        this.playedOn = playedOn == null ? "" : playedOn.toString(DATETIME_YYYY_MM_DD_FORMAT);
        this.playedFiles = playedFiles;
    }

}
