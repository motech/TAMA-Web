package org.motechproject.tamadomain.builder;

import org.motechproject.tamadomain.domain.City;
import org.motechproject.tamadomain.domain.Clinic;

import java.util.Arrays;

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

    public ClinicBuilder withClinicianContacts(Clinic.ClinicianContact... contacts) {
        this.clinic.setClinicianContacts(Arrays.asList(contacts));
        return this;
    }

    public ClinicBuilder withDefaults() {
        Clinic.ClinicianContact contact = new Clinic.ClinicianContact("drpujari","0987654321");
        this.withName("DefaultName")
                .withPhoneNumber("1234567890")
                .withAddress("DefaultAddress")
                .withCity(City.newCity("Pune"))
                .withClinicianContacts(contact);
        return this;
    }
}
