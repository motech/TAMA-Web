package org.motechproject.tama.fourdayrecall.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'WeeklyAdherenceLog'")
public class WeeklyAdherenceLog extends CouchEntity {

    private String patientId;
    private String treatmentAdviceId;
    private LocalDate weekStartDate;
    private LocalDate logDate;
    private int numberOfDaysMissed;

    public WeeklyAdherenceLog() {
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

    public static boolean logExists(AllWeeklyAdherenceLogs allWeeklyAdherenceLogs, String patientId, String treatmentAdviceId, LocalDate weekStartDate) {
        return allWeeklyAdherenceLogs.findLogsByWeekStartDate(patientId, treatmentAdviceId, weekStartDate).size() > 0;
    }

    public static WeeklyAdherenceLog create(String patientId, String treatmentAdviceDocId, LocalDate startDateForAnyWeek, int numberOfDaysMissed) {
        return new WeeklyAdherenceLog(patientId,
                treatmentAdviceDocId,
                startDateForAnyWeek,
                DateUtil.today(), numberOfDaysMissed);
    }
}
