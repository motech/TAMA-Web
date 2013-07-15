package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class DosageAdherenceLogSummary {
    private LocalDate dosageDate;
    private DosageStatus dosageStatus;
    private Time dosageTime;
    private String regimenId;
    private String treatmentAdviceId;

    public LocalDate getDosageDate() {
        return dosageDate;
    }

    public DosageAdherenceLogSummary setDosageDate(LocalDate dosageDate) {
        this.dosageDate = dosageDate;
        return this;
    }

    public DosageStatus getDosageStatus() {
        return dosageStatus;
    }

    public DosageAdherenceLogSummary setDosageStatus(DosageStatus dosageStatus) {
        this.dosageStatus = dosageStatus;
        return this;
    }

    public Time getDosageTime() {
        return dosageTime;
    }

    public DosageAdherenceLogSummary setDosageTime(Time dosageTime) {
        this.dosageTime = dosageTime;
        return this;
    }
    public DosageAdherenceLogSummary setRegimenId(String regimenId) {
        this.regimenId = regimenId;
        return this;
    }
    public String getRegimenId()
    {
        return regimenId;

    }


    public String getTreatmentAdviceId() {
        return treatmentAdviceId;
    }

    public DosageAdherenceLogSummary setTreatmentAdviceId(String treatmentAdviceId)
    {
        this.treatmentAdviceId = treatmentAdviceId;
        return this;

    }
}
