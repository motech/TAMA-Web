package org.motechproject.tama.outbox.web;

import lombok.Getter;
import org.joda.time.DateTime;

import java.util.List;

public class OutboxMessageLogRow {
    @Getter DateTime created;
    @Getter DateTime played;
    @Getter List<String> audioFilesPlayed;

    public OutboxMessageLogRow(DateTime created, DateTime played, List<String> audioFilesPlayed) {
        this.created = created;
        this.played = played;
        this.audioFilesPlayed = audioFilesPlayed;
    }
}
