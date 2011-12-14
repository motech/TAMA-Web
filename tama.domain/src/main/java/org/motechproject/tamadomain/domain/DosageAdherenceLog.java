package org.motechproject.tamadomain.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.tamacommon.domain.CouchEntity;

@TypeDiscriminator("doc.documentType == 'DosageAdherenceLog'")
public class DosageAdherenceLog extends CouchEntity {

    private String patientId;

    private String regimenId;

    private String dosageId;

    private LocalDate dosageDate;

    private DosageStatus dosageStatus;

    private DosageNotTakenReason reason;

    private boolean dosageTakenLate;

    public DosageAdherenceLog() {
    }

    public DosageAdherenceLog(String patientId, String regimenId, String dosageId, DosageStatus dosageStatus, LocalDate dosageDate) {
        this.patientId = patientId;
        this.regimenId = regimenId;
        this.dosageId = dosageId;
        this.dosageDate = dosageDate;
        this.dosageStatus = dosageStatus;
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
        this.dosageTakenLate = true;
    }

    public void setDosageTakenLate(boolean value){
        dosageTakenLate = value;
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
}
