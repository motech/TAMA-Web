package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

import javax.validation.constraints.NotNull;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'DosageTimeSlot'")
public class DosageTimeSlot extends CouchEntity {

    private TimeOfDay dosageTime;

    @NotNull
    private List<String> patientDocumentIds;

    public TimeOfDay getDosageTime() {
        return dosageTime;
    }

    public void setDosageTime(TimeOfDay dosageTime) {
        this.dosageTime = dosageTime;
    }

    public List<String> getPatientDocumentIds() {
        return patientDocumentIds;
    }

    public void setPatientDocumentIds(List<String> patientDocumentIds) {
        this.patientDocumentIds = patientDocumentIds;
    }

}
