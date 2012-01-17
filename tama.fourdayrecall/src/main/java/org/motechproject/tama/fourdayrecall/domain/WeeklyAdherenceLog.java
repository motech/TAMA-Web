package org.motechproject.tama.fourdayrecall.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'WeeklyAdherenceLog'")
public class WeeklyAdherenceLog extends CouchEntity {

    private String patientId;
    private String treatmentAdviceId;
    private LocalDate weekStartDate;
    private LocalDate logDate;
    private int numberOfDaysMissed;
    private boolean notResponded;

    public WeeklyAdherenceLog() {
        notResponded = false;
    }

    public WeeklyAdherenceLog(String patientId, String treatmentAdviceDocId, LocalDate weekStartDate, LocalDate logDate, int numberOfDaysMissed) {
        this.patientId = patientId;
        this.treatmentAdviceId = treatmentAdviceDocId;
        this.weekStartDate = weekStartDate;
        this.logDate = logDate;
        this.numberOfDaysMissed = numberOfDaysMissed;
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

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public static WeeklyAdherenceLog create(String patientId, String treatmentAdviceDocId, LocalDate startDateForAnyWeek, int numberOfDaysMissed) {
        return new WeeklyAdherenceLog(patientId,
                treatmentAdviceDocId,
                startDateForAnyWeek,
                DateUtil.today(), numberOfDaysMissed);
    }

    public static WeeklyAdherenceLog create(String patientId, String treatmentAdviceDocId, LocalDate startDateForAnyWeek, int numberOfDaysMissed, LocalDate logDate) {
        return new WeeklyAdherenceLog(patientId,
                treatmentAdviceDocId,
                startDateForAnyWeek,
                logDate, numberOfDaysMissed);
    }

    public void setNotResponded(boolean notResponded) {
        this.notResponded = notResponded;
    }

    public boolean getNotResponded() {
        return this.notResponded;
    }

    public void merge(WeeklyAdherenceLog that) {
        this.setNotResponded(that.getNotResponded());
        this.setLogDate(that.getLogDate());
        this.setNumberOfDaysMissed(that.getNumberOfDaysMissed());
        this.setTreatmentAdviceId(that.getTreatmentAdviceId());
        this.setPatientId(that.getPatientId());
        this.setWeekStartDate(that.getWeekStartDate());
    }
}
