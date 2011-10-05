package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;

@TypeDiscriminator("doc.documentType == 'WeeklyAdherenceLog'")
public class WeeklyAdherenceLog extends CouchEntity {

    private String patientId;
    private LocalDate logDate;
    private int numberOfDaysMissed;
    private String treatmentAdviceId;

    public WeeklyAdherenceLog() {
    }

    public WeeklyAdherenceLog(String patientId, LocalDate logDate, int numberOfDaysMissed, String treatmentAdviceDocId) {
        this.patientId = patientId;
        this.logDate = logDate;
        this.numberOfDaysMissed = numberOfDaysMissed;
        this.treatmentAdviceId = treatmentAdviceDocId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public int getNumberOfDaysMissed() {
        return numberOfDaysMissed;
    }

    public void setNumberOfDaysMissed(int numberOfDaysMissed) {
        this.numberOfDaysMissed = numberOfDaysMissed;
    }

    public String getTreatmentAdviceId() {
        return treatmentAdviceId;
    }

    public void setTreatmentAdviceId(String treatmentAdviceId) {
        this.treatmentAdviceId = treatmentAdviceId;
    }
}
