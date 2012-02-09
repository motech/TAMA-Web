package org.motechproject.tama.patient.builder;

import org.motechproject.tama.patient.domain.DosageTimeSlot;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;

public class DosageTimeSlotBuilder {

    private DosageTimeSlot dosageTimeSlot = new DosageTimeSlot();


    public static DosageTimeSlotBuilder startRecording() {
        return new DosageTimeSlotBuilder();
    }

    public DosageTimeSlot build() {
        return this.dosageTimeSlot;
    }

    public DosageTimeSlotBuilder withDefaults() {
        return this.withDosageTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).withPatientDocumentId("patientId");
    }

    public DosageTimeSlotBuilder withDosageTime(TimeOfDay dosageTime) {
        dosageTimeSlot.setDosageTime(dosageTime);
        return this;
    }

    public DosageTimeSlotBuilder withPatientDocumentId(String patientDocId) {
        dosageTimeSlot.setPatientDocumentId(patientDocId);
        return this;
    }

}
