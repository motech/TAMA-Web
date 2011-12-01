package org.motechproject.tamadomain.builder;

import org.motechproject.tamadomain.domain.Clinic;
import org.motechproject.tamadomain.domain.Clinician;

import java.util.Calendar;
import java.util.Date;

public class ClinicianBuilder {

    private Clinician clinician = new Clinician();

    public static ClinicianBuilder startRecording() {
        return new ClinicianBuilder();
    }

    public ClinicianBuilder withName(String name) {
        clinician.setName(name);
        return this;
    }

    public ClinicianBuilder withUserName(String username) {
        clinician.setUsername(username);
        return this;
    }

    public ClinicianBuilder withContactNumber(String contactNumber) {
        clinician.setContactNumber(contactNumber);
        return this;
    }

    public ClinicianBuilder withAlternateContactNumber(String alternateContactNumber) {
        clinician.setAlternateContactNumber(alternateContactNumber);
        return this;
    }

    public ClinicianBuilder withPassword(String password) {
        clinician.setPassword(password);
        return this;
    }

    public ClinicianBuilder withClinic(Clinic clinic) {
        clinician.setClinic(clinic);
        return this;
    }

    public Clinician build() {
        return this.clinician;
    }

    public ClinicianBuilder withDefaults() {
        String validContactNumber = "1234567890";
        String validAlternateContactNumber = "1234567890";
        Date time = Calendar.getInstance().getTime();
        String username = "test" + time.getTime();
        String password = "test";
        String name = "testName" + time.getTime();
        return ClinicianBuilder.startRecording().
                withName(name).
                withAlternateContactNumber(validAlternateContactNumber).
                withContactNumber(validContactNumber).
                withPassword(password).
                withUserName(username).
                withClinic(ClinicBuilder.startRecording().withDefaults().build());
    }
}
