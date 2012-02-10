package org.motechproject.tama.patient.builder;

import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
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
        return this.withCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).withPatientDocumentId("patientId");
    }

    public CallTimeSlotBuilder withCallTime(TimeOfDay callTime) {
        callTimeSlot.setCallTime(callTime);
        return this;
    }

    public CallTimeSlotBuilder withPatientDocumentId(String patientDocId) {
        callTimeSlot.setPatientDocumentId(patientDocId);
        return this;
    }

}
