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

    public ClinicBuilder withPhoneNumber(String phoneNumber) {
        this.clinic.setPhone(phoneNumber);
        return this;
    }

    public ClinicBuilder withAddress(String address) {
        this.clinic.setAddress(address);
        return this;
    }

    public ClinicBuilder withCity(City city) {
        this.clinic.setCity(city);
        return this;
    }

    public ClinicBuilder withDefaults() {
        this.withName("DefaultName")
                .withPhoneNumber("1234567890")
                .withAddress("DefaultAddress")
                .withCity(City.newCity("Pune"));
        return this;
    }
}
