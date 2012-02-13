package org.motechproject.tama.dailypillreminder.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.util.DateUtil;

public class DosageAdherenceLogBuilder {

    private DosageAdherenceLog adherenceLog = new DosageAdherenceLog(null, null, null, null, null, null, null, null);

    public DosageAdherenceLog build() {
        return this.adherenceLog;
    }

    public DosageAdherenceLogBuilder withDefaults() {
        this.withPatientId("12345")
                .withRegimenId("56789")
                .withDosageId("123")
                .withDosageDate(DateUtil.newDate(2001, 1, 1))
                .withDosageStatus(DosageStatus.TAKEN);
        return this;
    }

    public DosageAdherenceLogBuilder withDosageStatus(DosageStatus dosageStatus) {
        adherenceLog.setDosageStatus(dosageStatus);
        return this;
    }

    public DosageAdherenceLogBuilder withDosageDate(LocalDate dosageDate) {
        adherenceLog.setDosageDate(dosageDate);
        return this;
    }

    public DosageAdherenceLogBuilder withDosageId(String dosageId) {
        adherenceLog.setDosageId(dosageId);
        return this;
    }

    public DosageAdherenceLogBuilder withRegimenId(String regimenId) {
        adherenceLog.setRegimenId(regimenId);
        return this;
    }

    public DosageAdherenceLogBuilder withPatientId(String patientId) {
        adherenceLog.setPatientId(patientId);
        return this;
    }

    public DosageAdherenceLogBuilder withTreatmentAdviceId(String treatmentAdviceId) {
        adherenceLog.setTreatmentAdviceId(treatmentAdviceId);
        return this;
    }

    public static DosageAdherenceLogBuilder startRecording() {
        return new DosageAdherenceLogBuilder();
    }

    public DosageAdherenceLogBuilder withDoseTakenLate() {
        adherenceLog.setDosageTakenLate(true);
        return this;
    }
}
