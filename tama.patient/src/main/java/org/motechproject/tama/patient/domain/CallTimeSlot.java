package org.motechproject.tama.patient.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.domain.TimeOfDay;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'CallTimeSlot'")
public class CallTimeSlot extends CouchEntity {

    private TimeOfDay callTime;

    @NotNull
    private String patientDocumentId;

    public TimeOfDay getCallTime() {
        return callTime;
    }

    public void setCallTime(TimeOfDay callTime) {
        this.callTime = callTime;
    }

    public String getPatientDocumentId() {
        return patientDocumentId;
    }

    public void setPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
    }

}
