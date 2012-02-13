package org.motechproject.tama.dailypillreminder.domain;

public class DosageAdherenceLogSummary {
    private String id;
    private String treatmentAdviceId;
    private String dosageStatus;

    public String getId() {
        return id;
    }

    public DosageAdherenceLogSummary setId(String id) {
        this.id = id;
        return this;
    }

    public String getTreatmentAdviceId() {
        return treatmentAdviceId;
    }

    public DosageAdherenceLogSummary setTreatmentAdviceId(String treatmentAdviceId) {
        this.treatmentAdviceId = treatmentAdviceId;
        return this;
    }

    public String getDosageStatus() {
        return dosageStatus;
    }

    public DosageAdherenceLogSummary setDosageStatus(String dosageStatus) {
        this.dosageStatus = dosageStatus;
        return this;
    }
}
