package org.motechproject.tama.builder;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;

public class ClinicBuilder {

    private Clinic clinic = Clinic.newClinic();

    public Clinic build() {
        return this.clinic;
    }


    public static ClinicBuilder startRecording() {
        return  new ClinicBuilder();
    }

    public ClinicBuilder withId(String id) {
        this.clinic.setId(id);
        return this;
    }

    public ClinicBuilder withName(String testName) {
        this.clinic.setName(testName);
        return this;
    }

    private ClinicBuilder withPhoneNumber(String phoneNumber) {
        this.clinic.setPhone(phoneNumber);
        return this;
    }

    private ClinicBuilder withAddress(String address) {
        this.clinic.setAddress(address);
        return this;
    }

    private ClinicBuilder withCity(String city) {
        this.clinic.setCity(City.newCity(city));
        return this;
    }

    public ClinicBuilder withDefaults() {
        this.withName("DefaultName")
                .withPhoneNumber("1234567890")
                .withAddress("DefaultAddress")
                .withCity("Pune");
        return this;
    }
}
