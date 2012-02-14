package org.motechproject.tama.patient.builder;

import org.joda.time.LocalTime;
import org.motechproject.tama.patient.domain.CallTimeSlot;

public class CallTimeSlotBuilder {

    private CallTimeSlot callTimeSlot = new CallTimeSlot();


    public static CallTimeSlotBuilder startRecording() {
        return new CallTimeSlotBuilder();
    }

    public CallTimeSlot build() {
        return this.callTimeSlot;
    }

    public CallTimeSlotBuilder withDefaults() {
        return this.withCallTime(new LocalTime(10, 0)).withPatientDocumentId("patientId");
    }

    public CallTimeSlotBuilder withCallTime(LocalTime callTime) {
        callTimeSlot.setCallTime(callTime);
        return this;
    }

    public CallTimeSlotBuilder withPatientDocumentId(String patientDocId) {
        callTimeSlot.setPatientDocumentId(patientDocId);
        return this;
    }

}
