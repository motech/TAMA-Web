package org.motechproject.tama.healthtips.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'HealthTipsHistory'")
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
