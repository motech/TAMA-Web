package org.motechproject.tamafunctional.testdata;

public class TestClinic extends TestEntity {
    private String name;
    private String phoneNumber;
    private String address;
    private String city;

    private TestClinic() {
    }

    public static TestClinic withMandatory() {
        return new TestClinic().name(unique("DefaultName")).phoneNumber("1234567890").address("DefaultAddress")
                .city("Pune");
    }

    public String name() {
        return name;
    }

    public TestClinic name(String name) {
        this.name = name;
        return this;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public TestClinic phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String address() {
        return address;
    }

    public TestClinic address(String address) {
        this.address = address;
        return this;
    }

    public String city() {
        return city;
    }

    public TestClinic city(String city) {
        this.city = city;
        return this;
    }

    @Override
    public String resourceName() {
        return "clinics";
    }
}
