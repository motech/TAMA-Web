package org.motechproject.tama.builder;

import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.util.DateUtility;

import java.util.Date;

public class DosageAdherenceLogBuilder {

    private DosageAdherenceLog adherenceLog = new DosageAdherenceLog();

    public DosageAdherenceLog build() {
        return this.adherenceLog;
    }

    public DosageAdherenceLogBuilder withDefaults() {
        this
                .withPatientId("12345")
                .withRegimenId("56789")
                .withDosageId("123")
                .withDosageDate(DateUtility.newDate(2001, 01, 01))
                .withDosageStatus(DosageStatus.TAKEN);
        return this;
    }

    public DosageAdherenceLogBuilder withDosageStatus(DosageStatus dosageStatus) {
        adherenceLog.setDosageStatus(dosageStatus);
        return this;
    }

    public DosageAdherenceLogBuilder withDosageDate(Date dosageDate) {
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
}
