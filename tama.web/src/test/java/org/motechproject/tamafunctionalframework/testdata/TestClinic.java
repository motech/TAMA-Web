package org.motechproject.tamafunctionalframework.testdata;

public class TestClinic extends TestEntity {
    private String name;
    private String phoneNumber;
    private String address;
    private String city;
    private String clinicianContact0Name;
    private String clinicianContact0Number;
    private String clinicianContact1Name;
    private String clinicianContact1Number;
    private String clinicianContact2Name;
    private String clinicianContact2Number;

    public String clinicianContact0Name() {
        return clinicianContact0Name;
    }

    public TestClinic clinicianContact0Name(String clinicianContact0Name) {
        this.clinicianContact0Name = clinicianContact0Name;
        return this;
    }

    public String clinicianContact0Number() {
        return clinicianContact0Number;
    }

    public TestClinic clinicianContact0Number(String clinicianContact0Number) {
        this.clinicianContact0Number = clinicianContact0Number;
        return this;
    }

    public String clinicianContact1Name() {
        return clinicianContact1Name;
    }

    public TestClinic clinicianContact1Name(String clinicianContact1Name) {
        this.clinicianContact1Name = clinicianContact1Name;
        return this;
    }

    public String clinicianContact1Number() {
        return clinicianContact1Number;
    }

    public TestClinic clinicianContact1Number(String clinicianContact1Number) {
        this.clinicianContact1Number = clinicianContact1Number;
        return this;
    }

    public String clinicianContact2Name() {
        return clinicianContact2Name;
    }

    public TestClinic clinicianContact2Name(String clinicianContact2Name) {
        this.clinicianContact2Name = clinicianContact2Name;
        return this;
    }

    public String clinicianContact2Number() {
        return clinicianContact2Number;
    }

    public TestClinic clinicianContact2Number(String clinicianContact2Number) {
        this.clinicianContact2Number = clinicianContact2Number;
        return this;
    }

    private TestClinic() {
    }

    public static TestClinic withMandatory() {
        return new TestClinic()
                .name(unique("DefaultName"))
                .phoneNumber("1234567890")
                .address("DefaultAddress")
                .city("Pune")
                .clinicianContact0Name("pujari")
                .clinicianContact0Number("1111111111")
                .clinicianContact1Name("pujari1")
                .clinicianContact1Number("2222222222")
                .clinicianContact2Name("pujari2")
                .clinicianContact2Number("3333333333");
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
