package org.motechproject.tama.dailypillreminder.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'DosageAdherenceLog'")
public class DosageAdherenceLog extends CouchEntity {

    private String patientId;

    private String regimenId;

    private String dosageId;

    private LocalDate dosageDate;

    private DosageStatus dosageStatus;

    private DosageNotTakenReason reason;

    private boolean dosageTakenLate;

    private DateTime dosageStatusUpdatedAt;

    public DosageAdherenceLog() {
        dosageStatusUpdatedAt = DateUtil.now();
    }

    public DosageAdherenceLog(String patientId, String regimenId, String dosageId, DosageStatus dosageStatus, LocalDate dosageDate) {
        this(patientId, regimenId, dosageId, dosageStatus, dosageDate, DateUtil.now());
    }
    public DosageAdherenceLog(String patientId, String regimenId, String dosageId, DosageStatus dosageStatus, LocalDate dosageDate, DateTime dosageStatusUpdatedAt) {
        this.patientId = patientId;
        this.regimenId = regimenId;
        this.dosageId = dosageId;
        this.dosageDate = dosageDate;
        this.dosageStatus = dosageStatus;
        this.dosageStatusUpdatedAt = dosageStatusUpdatedAt;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRegimenId() {
        return regimenId;
    }

    public void setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }

    public String getDosageId() {
        return dosageId;
    }

    public void setDosageId(String dosageId) {
        this.dosageId = dosageId;
    }

    public LocalDate getDosageDate() {
        return dosageDate;
    }

    public void setDosageDate(LocalDate dosageDate) {
        this.dosageDate = dosageDate;
    }

    public DosageStatus getDosageStatus() {
        return dosageStatus;
    }

    public boolean isDosageTakenLate() {
        return dosageTakenLate;
    }

    public void setDosageStatus(DosageStatus dosageStatus) {
        this.dosageStatus = dosageStatus;
    }

    public DosageNotTakenReason getReason() {
        return reason;
    }

    public void setReason(DosageNotTakenReason reason) {
        this.reason = reason;
    }

    public void dosageIsTakenLate() {
        if (DosageStatus.TAKEN.equals(dosageStatus)) this.dosageTakenLate = true;
    }

    public void setDosageTakenLate(boolean value) {
        dosageTakenLate = value;
    }

    public DateTime getDosageStatusUpdatedAt() {
        return dosageStatusUpdatedAt == null ? null : DateUtil.setTimeZone(dosageStatusUpdatedAt);
    }

    public void setDosageStatusUpdatedAt(DateTime dosageStatusUpdatedAt) {
        this.dosageStatusUpdatedAt = dosageStatusUpdatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DosageAdherenceLog that = (DosageAdherenceLog) o;

        if (dosageDate != null ? !dosageDate.equals(that.dosageDate) : that.dosageDate != null) return false;
        if (dosageId != null ? !dosageId.equals(that.dosageId) : that.dosageId != null) return false;
        if (dosageStatus != that.dosageStatus) return false;
        if (reason != that.reason) return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;
        if (regimenId != null ? !regimenId.equals(that.regimenId) : that.regimenId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        result = 31 * result + (regimenId != null ? regimenId.hashCode() : 0);
        result = 31 * result + (dosageId != null ? dosageId.hashCode() : 0);
        result = 31 * result + (dosageDate != null ? dosageDate.hashCode() : 0);
        result = 31 * result + (dosageStatus != null ? dosageStatus.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }

    public static DosageAdherenceLog create(String patientId, String regimenId, DosageStatus dosageStatus, Dose dose, DateTime doseTakenTime, int dosageInterval) {
        DosageAdherenceLog adherenceLog = new DosageAdherenceLog(patientId, regimenId, dose.getDosageId(), dosageStatus, dose.getDate());
        if (dose.isLateToTake(doseTakenTime, dosageInterval)) adherenceLog.dosageIsTakenLate();
        return adherenceLog;
    }

    public void updateStatus(DosageStatus status, DateTime doseTakenTime, int dosageInterval, Dose dose) {
        setDosageStatus(status);
        if (dose.isLateToTake(doseTakenTime, dosageInterval)) dosageIsTakenLate();
        setDosageStatusUpdatedAt(doseTakenTime);
    }
}
