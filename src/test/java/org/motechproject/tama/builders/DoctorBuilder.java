package org.motechproject.tama.builders;

import org.motechproject.tama.domain.Doctor;

public class DoctorBuilder {

    private Doctor doctor = new Doctor();

    public DoctorBuilder withId(String id) {
        doctor.setId(id);
        return this;
    }

    public DoctorBuilder withFirstName(String firstName) {
        doctor.setFirstName(firstName);
        return this;
    }

    public Doctor build() {
        return this.doctor;
    }

    public static DoctorBuilder startRecording() {
        return new DoctorBuilder();
    }
}
