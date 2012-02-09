package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'DosageTimeSlot'")
public class DosageTimeSlot extends CouchEntity {

    private TimeOfDay dosageTime;

    @NotNull
    private String patientDocumentId;

    public TimeOfDay getDosageTime() {
        return dosageTime;
    }

    public void setDosageTime(TimeOfDay dosageTime) {
        this.dosageTime = dosageTime;
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

}
