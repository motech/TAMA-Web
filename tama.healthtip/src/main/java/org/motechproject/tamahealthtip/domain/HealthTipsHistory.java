package org.motechproject.tamahealthtip.domain;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class HealthTipsHistory extends CouchEntity {

    @NotNull
    private String audioFilename;

    @NotNull
    private String patientDocumentId;

    @NotNull
    private DateTime lastPlayed;

    public HealthTipsHistory() {
    }

    public HealthTipsHistory(String patientDocumentId, String audioFilename, DateTime lastPlayed) {
        this.audioFilename = audioFilename;
        this.lastPlayed = lastPlayed;
        this.patientDocumentId = patientDocumentId;
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

    public String getAudioFilename() {
        return audioFilename;
    }

    public void setAudioFilename(String audioFilename) {
        this.audioFilename = audioFilename;
    }

    public DateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(DateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
